
## MoveStats: Android Fitness App with Modular Architecture

MoveStats is an Android application designed to help users adopt a healthier lifestyle by tracking their daily and weekly exercise routines. The app leverages a modular architecture for scalability and maintainability.

### Features

* **Activity Recognition:** Utilizes machine learning to classify physical activities (walking, running, climbing stairs, etc.) based on phone sensor data (accelerometer and gyroscope).
* **Calorie Tracking:** Estimates the number of calories burned based on activity type and intensity.
* **Leaderboards:** Fosters healthy competition by showcasing user rankings. 
* **Data Collection:** Allows users to contribute data for model improvement. Users can choose between timed (20 seconds) or free-form data collection.
* **Activity History:** Provides a record of user exercise history for informed decisions. Data is visualized using open-source library MPAndroidChart ([https://github.com/PhilJay/MPAndroidChart/releases](https://github.com/PhilJay/MPAndroidChart/releases)).

### Technical Details

**Modular Architecture:**

MoveStats is designed with a modular architecture to promote code reusability, easier maintenance, and potential future expansion. Here's a breakdown of the key components:

* **Android Application:** The user interface that interacts with the user and provides features like activity tracking, data visualization, and leaderboards.
* **Data Collection Module:** Handles sensor data acquisition (accelerometer, gyroscope) and offers options for timed or free-form collection.
* **Machine Learning Module:** Encompasses the pre-trained Multi-Layer Perceptron model responsible for activity classification based on sensor data.
* **API Communication Module:** Facilitates communication with the Firebase backend for data storage (user information, activity history) and retrieval of model predictions.
* **Firebase Backend:** Serves as the data storage solution, managing user accounts, historical activity data, and acting as an intermediary for model predictions.
* **Background Data Collection (optional):** Utilizes WorkManager to collect sensor data in 20-second intervals over a 5-minute period for continuous activity recognition. The most frequent prediction within each 20-second window is considered the dominant activity. This helps in building a better model.

**Modular Architecture Diagram**

[Image of Architecture Diagram](images/architecture.png)  **Additional Technical Details:**

* **Platform:** Android
* **Programming Language:** Kotlin
* **Machine Learning Model:** Multi-Layer Perceptron (**Accuracy: 92%** on initial testing)
* **Sensors:** Accelerometer, Gyroscope
* **Database:** Firebase

### Future Work

* Implement motivational features to encourage physical activity (e.g., gamification, rewards).
* Gather additional data to enhance the machine learning model's accuracy.
* Integrate new functionalities, such as personalized workout plans.

