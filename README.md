# ☁️ Weather App — Java Swing

A desktop weather application built with Java Swing that fetches real-time weather data using the OpenWeatherMap API.

> Built with AI assistance (Claude) and customized for learning purposes.

---

## 📸 Preview

> Search any city and get instant weather details in a clean dark-themed UI.

---

## ✨ Features

- 🔍 Search weather by city name
- 🌡️ Temperature, Feels Like, Humidity, Wind Speed
- 🌤️ Weather condition with emoji indicators
- ⚡ Non-blocking API calls using SwingWorker
- 🎨 Dark-themed modern UI

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| Java Swing | Desktop UI framework |
| HttpURLConnection | HTTP API calls |
| org.json | JSON parsing |
| OpenWeatherMap API | Live weather data |

---

## 🚀 How to Run

### Prerequisites
- Java 17 or above
- [org.json jar](https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar)
- OpenWeatherMap API key (free at [openweathermap.org](https://openweathermap.org))

### Steps

```bash
# Compile
javac -cp .;json-20231013.jar WeatherApp.java   # Windows
javac -cp .:json-20231013.jar WeatherApp.java   # Mac/Linux

# Run
java -cp .;json-20231013.jar WeatherApp         # Windows
java -cp .:json-20231013.jar WeatherApp         # Mac/Linux
```

### In IntelliJ IDEA
1. Open project
2. Add `json-20231013.jar` via **File → Project Structure → Libraries**
3. Run `WeatherApp.java`

---

## 📂 Project Structure
