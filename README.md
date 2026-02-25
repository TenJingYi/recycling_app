## Prototype Documentation
  ​**Project Title:** EcoWorld
  
  **​Team Name:** DevSpark
  
  **Target SDG:** Goal 12: Responsible Consumption and Production  

## 1.​ Technical Architecture
​Our team utilized a robust Google-powered stack to ensure scalability and efficiency:  
  * **​Frontend:** Built with Android Studio (Java) for a native, responsive mobile experience.  

  * **AI Backend:** Gemini API (via Google AI Studio) acts as the "Quest Master" to identify waste and provide educational feedback.  

  * ​**Location Services:** Integrated Google Maps API to visualize and navigate users to verified recycling centers in Malaysia.  

  * **​Data & Auth:** Firebase Realtime Database stores player XP, levels (5 Levels from Seedling to Forest Protector), and achievement badges.

  
## 2.​ Implementation Details
  * **AI Validation:** Instead of static lists, users submit descriptions/images of waste. Gemini AI verifies recyclability and explains why, reducing stream contamination.  

  * **"Duolingo-style" Gamification:** To solve the problem of low motivation, we implemented interactive Education Quizzes and a progression system with badges like "Recycle Rookie" and "Quiz Whiz."

  * **Dynamic Mapping:** The app automatically filters and displays the nearby recycling centers based on the user's real-time GPS coordinates.

  
## 3. Challenges Faced
  * **Major Technical Hurdle:** As first-year students, connecting the Google Maps API to filter locations dynamically in Java was our biggest obstacle.

  * **Resolution:** We overcame this by attending the KitaHack Web Development & APIs Workshop (Jan 31st) and implementing custom marker filtering logic based on geographical proximity.  

  * **Strategic Pivot:** We originally planned to include carbon footprint tracking (SDG 13) but decided to pivot exclusively to SDG 12 to ensure a higher quality, more complete prototype for the preliminary round.  


## 4.​ Future Roadmap
  * **Scalability:** Migrating the database to Google Cloud Firestore for better handling of larger user volumes.  

  * ​**Community Features:** Adding local leaderboards to foster neighborhood recycling competitions.  

  * **Expanded AI:** Integrating image recognition (Gemini Vision) to identify waste via the camera automatically.
