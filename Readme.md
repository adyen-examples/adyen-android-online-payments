# Adyen [online payment](https://docs.adyen.com/checkout) integration for Android

This repository includes examples of Android application integrations for online payments with Adyen. Within this demo app, you'll find a simplified version of an e-commerce cart and checkout, complete with commented code to highlight key features and concepts of Adyen's API. Have a look at the underlying code to see how you can integrate Adyen in your Android application to give your shoppers the option to pay with their preferred payment methods, all in a seamless checkout experience. The Android integration is done using Kotlin.

![Card checkout demo](./cardcheckout.gif)

## Supported Demo Integrations

Make sure the payment methods you want to use in the demo are enabled for your account. Refer to the [documentation](https://docs.adyen.com/payment-methods#add-payment-methods-to-your-account) to add missing payment methods.

Demos of the following Android client-side integrations are currently available in this repository:

- [Drop-in](https://docs.adyen.com/checkout/drop-in-web)

  - [x] Ideal
  - [x] Credit card
  - [x] Credit card 3DS2
  - [x] PaySafeCard
  - [x] Klarna Pay later

- [Component](https://docs.adyen.com/checkout/components-web)
  - [x] Ideal
  - [x] Credit card
  - [x] Credit card 3DS2

Each demo leverages Adyen's Library for Android ([GitHub](https://github.com/Adyen/adyen-android) | [Docs](https://docs.adyen.com/checkout/android)) and the Golang API library ([GitHub](https://github.com/Adyen/adyen-go-api-library) | [Docs](https://docs.adyen.com/development-resources/libraries#go)) on the server side.

## Requirements

- Android 9+
- Android Studio
- Android Emulator

## Installation & Usage

1. Clone this repo:

```
git clone https://github.com/adyen-examples/adyen-android-online-payments.git
```

2. Navigate to the `adyen-android-online-payments/server` directory to configure and run the backend server:

   1. Create a `.env` file with your [API key](https://docs.adyen.com/user-management/how-to-get-the-api-key), [Client public Key](https://docs.adyen.com/checkout/android/drop-in#get-your-client-encryption-public-key), server URL, and merchant account name (all credentials are in string format):

      If you are using the emulator, the server url can be `http://10.0.2.2:3000`. If you use a real Android device, connect it to same network you have the server running and use the local IP of your network or WiFi router instead of `10.0.2.2`

      ```
      ADYEN_API_KEY="YOUR_API_KEY"
      ADYEN_MERCHANT="YOUR_MERCHANT_ACCOUNT"
      CLIENT_PUBLIC_KEY="YOUR_CLIENT_PUBLIC_KEY"
      SERVER_URL="YOUR_BACKEND_SERVER_URL"
      ```

   2. Run the backend server:

      We have a minimal server written in Golang. The server runs on port 3000.

      You can run the prebuilt binary if you don't have Go installed.

      ```bash
      # on mac
      ./app-macOS
      # on Linux
      ./app-linux
      ```

      You can also run `go run -v .` if you have Go installed.

3. Build & Start the Android app:

   Open the project in Android Studio and run the `app/src/main/java/com/example/adyen/checkout/MainActivity.kt` from the context menu. To do this, you must configure an emulator or setup a real device on Android Studio. You can follow [this guide](https://developer.android.com/training/basics/firstapp/running-app) for instructions.

   Alternatively you can install the APK in the `app/release/` folder.

4. Visit the Android app called "Adyen Checkout Demo" on the emulated/real device and select Drop-in or Component from the bottom tab and choose a payment type.

   To try out integrations with test card numbers and payment method details, see [Test card numbers](https://docs.adyen.com/development-resources/test-cards/test-card-numbers).

## Contributing

We commit all our new features directly into our GitHub repository. Feel free to request or suggest new features or code changes yourself as well!

## License

MIT license. For more information, see the **LICENSE** file in the root directory.
