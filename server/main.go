package main

import (
	"fmt"
	"log"
	"net/http"
	"net/url"
	"os"
	"time"

	"github.com/adyen/adyen-go-api-library/src/adyen"
	"github.com/adyen/adyen-go-api-library/src/checkout"
	"github.com/adyen/adyen-go-api-library/src/common"
	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
)

var (
	client          *adyen.APIClient
	merchantAccount string
)

const PaymentDataCookie = "paymentData"

func handleError(method string, c *gin.Context, err error, httpRes *http.Response) {
	log.Printf("Error in %s: %s\n", method, err.Error())
	if httpRes != nil && httpRes.StatusCode >= 300 {
		c.JSON(httpRes.StatusCode, httpRes.Status)
		return
	}
	c.JSON(http.StatusBadRequest, err.Error())
}

func ConfigHandler(c *gin.Context) {
	c.Header("Content-Type", "application/json")
	c.JSON(http.StatusOK, map[string]string{
		"environment":     "test",
		"serverURL":       os.Getenv("SERVER_URL"),
		"clientPublicKey": os.Getenv("CLIENT_PUBLIC_KEY"),
	})
	return
}

// PaymentMethodsHandler retrieves a list of available payment methods from Adyen API
func PaymentMethodsHandler(c *gin.Context) {
	c.Header("Content-Type", "application/json")
	req := checkout.PaymentMethodsRequest{
		MerchantAccount: merchantAccount,
		CountryCode:     "NL",
		Channel:         "Android",
	}

	log.Printf("Request for %s API::\n%+v\n", "PaymentMethods", req)
	res, httpRes, err := client.Checkout.PaymentMethods(&req)
	if err != nil {
		handleError("PaymentMethodsHandler", c, err, httpRes)
		return
	}
	c.JSON(http.StatusOK, res)
	return
}

// PaymentsHandler makes payment using Adyen API
func PaymentsHandler(c *gin.Context) {
	c.Header("Content-Type", "application/json")
	var req = checkout.PaymentRequest{}
	var paymentMethod map[string]interface{}
	if err := c.BindJSON(&paymentMethod); err != nil {
		handleError("PaymentsHandler", c, err, nil)
		return
	}
	req.PaymentMethod = paymentMethod
	req.MerchantAccount = merchantAccount
	req.Amount = checkout.Amount{Currency: "EUR", Value: 1000}
	req.Reference = fmt.Sprintf("%v", time.Now())
	req.ReturnUrl = "adyencheckout://com.example.adyen.checkout"

	log.Printf("Request for %s API::\n%+v\n", "Payments", req)
	res, httpRes, err := client.Checkout.Payments(&req)
	log.Printf("Response for %s API::\n%+v\n", "Payments", res)
	log.Printf("HTTP Response for %s API::\n%+v\n", "Payments", httpRes)
	if err != nil {
		handleError("PaymentsHandler", c, err, httpRes)
		return
	}
	if res.Action.PaymentData != "" {
		c.SetCookie(PaymentDataCookie, res.Action.PaymentData, 3600, "", "localhost", false, true)
	}
	c.JSON(http.StatusOK, res)
	return
}

// PaymentDetailsHandler gets payment details using Adyen API
func PaymentDetailsHandler(c *gin.Context) {
	c.Header("Content-Type", "application/json")
	var req checkout.DetailsRequest

	if err := c.BindJSON(&req); err != nil {
		handleError("PaymentDetailsHandler", c, err, nil)
		return
	}
	log.Printf("Request for %s API::\n%+v\n", "PaymentDetails", req)
	res, httpRes, err := client.Checkout.PaymentsDetails(&req)
	log.Printf("HTTP Response for %s API::\n%+v\n", "PaymentDetails", httpRes)
	if err != nil {
		handleError("PaymentDetailsHandler", c, err, httpRes)
		return
	}
	c.JSON(http.StatusOK, res)
	return
}

type Redirect struct {
	MD      string
	PaRes   string
	Payload string `form:"payload"`
}

// RedirectHandler handles POST and GET redirects from Adyen API
func RedirectHandler(c *gin.Context) {
	var redirect Redirect
	log.Println("Redirect received")

	if err := c.ShouldBind(&redirect); err != nil {
		handleError("RedirectHandler", c, err, nil)
		return
	}
	paymentData, err := c.Cookie(PaymentDataCookie)
	if err != nil {
		handleError("RedirectHandler", c, err, nil)
		return
	}
	var details map[string]interface{}
	if redirect.Payload != "" {
		details = map[string]interface{}{
			"payload": redirect.Payload,
		}
	} else {
		details = map[string]interface{}{
			"MD":    redirect.MD,
			"PaRes": redirect.PaRes,
		}
	}

	req := checkout.DetailsRequest{Details: details, PaymentData: paymentData}

	log.Printf("Request for %s API::\n%+v\n", "PaymentDetails", req)
	res, httpRes, err := client.Checkout.PaymentsDetails(&req)
	log.Printf("HTTP Response for %s API::\n%+v\n", "PaymentDetails", httpRes)

	if err != nil {
		handleError("RedirectHandler", c, err, httpRes)
		return
	}
	if res.PspReference != "" {
		c.Redirect(
			http.StatusFound,
			fmt.Sprintf("/?PspReference=%s&ResultCode=%s&RefusalReason=%s", url.QueryEscape(res.PspReference), url.QueryEscape(res.ResultCode), url.QueryEscape(res.RefusalReason)),
		)
		return
	}
	c.JSON(httpRes.StatusCode, httpRes.Status)
	return
}

func main() {
	// Set the router as the default one shipped with Gin
	router := gin.Default()

	godotenv.Load("./.env")

	client = adyen.NewClient(&common.Config{
		ApiKey:      os.Getenv("ADYEN_API_KEY"),
		Environment: common.TestEnv,
	})

	merchantAccount = os.Getenv("ADYEN_MERCHANT")

	router.GET("/api/handleShopperRedirect", RedirectHandler)
	router.POST("/api/handleShopperRedirect", RedirectHandler)

	// Setup route group and routes for the API
	api := router.Group("/api")

	api.GET("/getConfig", ConfigHandler)
	api.POST("/getPaymentMethods", PaymentMethodsHandler)
	api.POST("/initiatePayment", PaymentsHandler)
	api.POST("/submitAdditionalDetails", PaymentDetailsHandler)

	// Start and run the server
	router.Run(":3000")
}
