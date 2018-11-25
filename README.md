# Android Background Changer

This is an android app to control my wife's background (and send
beeps) via GCP pubsub. Lessons learned, using Dagger was overkill for this.

## Set up

1. Create a Google Cloud project called `PROJECT_ID`
2. Create a pubsub topic called `change_background` with a subscription called `change_background`
3. Create a service account and paste the JSON into `BACKGROUND_CHANGER_GOOGLE_APPLICATION_CREDENTIALS_JSON`
4. Add these values to `~/.gradle/gradle.properties`:

```
BACKGROUND_CHANGER_PUBSUB_PROJECT_ID="PROJECT_ID"
BACKGROUND_CHANGER_PUBSUB_SUBSCRIPTION_ID="change_background"
BACKGROUND_CHANGER_PUBSUB_TOPIC_ID="change_background"
BACKGROUND_CHANGER_GOOGLE_APPLICATION_CREDENTIALS_JSON="{ ... }"
```

* Run the "changer" app on the device to do the changing
* Run the "changee" app on the device whose background will be changed

## Warning

The "changee" app will kill your battery. It's a joke.
