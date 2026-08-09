# Associate Coding Exercise

### Tasks:

1. Load the card dataset from a REST API instead of the hardcoded list currently in use
2. Implement card delete feature
3. Make an improvement to the app

#### Objectives Achieved:

* The app was successfully reconfigured to connect to a REST API instead of using a hardcoded dataset
* Delete button UI logic and functions were written and now successfully remove cards from both the main screen and detailed view

#### Improvements:
* Added a logging interceptor to the network file so the full API response can be verified in the Logcat for easier debugging
* An additional button was made to restore deleted cards in the detailed view

## See the changes below:

<img width="25%" height="25%" alt="Flashcards Before 1" src="https://github.com/user-attachments/assets/41d22f49-87c4-4d23-9f44-e9449a95d24b" />

BEFORE - Flashcard app view before using a hardcoded list to populate the cards

<img width="25%" height="25%" alt="Flashcards Before 2" src="https://github.com/user-attachments/assets/99a0bf41-783f-44ec-b79d-c583fcc60f83" />

BEFORE - Detail view screen with no Delete or Restore buttons

<img width="25%" height="25%" alt="Flashcards After - Added API Connection" src="https://github.com/user-attachments/assets/ad1df9eb-5321-4046-9f26-f067594be7fa" />

AFTER - Flashcards are now loading via an API instead of the hardcoded list (note: the title headings are lower-cased in the API response, see confirmation in next photo)

<img width="25%" height="25%" alt="Flashcards Logcat API" src="https://github.com/user-attachments/assets/8971d2e2-fa7a-4a1b-a739-4e9929638f44" />

AFTER - The added Logging Interceptor shows the flashcards are actually coming from the network and not the old list

<img width="25%" height="25%" alt="Flashcards After - Added Delete and Restore" src="https://github.com/user-attachments/assets/0f27dffb-dff4-4d29-94db-4e411949a812" />

AFTER - Working Delete & Restore buttons successfully implemented in the detail view screen

https://github.com/user-attachments/assets/96228d77-d040-40c4-bb80-3519ec151de4

Demo video

