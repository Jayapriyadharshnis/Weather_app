import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;

public class WeatherApp extends JFrame {

    // ── UI Components ──
    private JTextField cityField;
    private JButton searchBtn;
    private JLabel cityLabel, tempLabel, descLabel, humidityLabel, feelsLabel, windLabel;
    private JPanel resultPanel;
    private JLabel statusLabel;

    // ── Colors ──
    private static final Color BG_DARK     = new Color(15, 23, 42);
    private static final Color CARD_BG     = new Color(30, 41, 59);
    private static final Color ACCENT      = new Color(56, 189, 248);
    private static final Color TEXT_WHITE  = new Color(241, 245, 249);
    private static final Color TEXT_MUTED  = new Color(148, 163, 184);
    private static final Color SUCCESS     = new Color(74, 222, 128);

    static String apiKey = "b25590dcd916e3a70c248058e33f11cd";

    public WeatherApp() {
        setTitle("Weather App");
        setSize(420, 580);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildResultPanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Top Panel: Title + Search ──
    private JPanel buildTopPanel() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(BG_DARK);
        top.setBorder(new EmptyBorder(30, 30, 20, 30));

        // Title
        JLabel title = new JLabel("☁  Weather");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Real-time city weather info");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(title);
        top.add(Box.createVerticalStrut(4));
        top.add(subtitle);
        top.add(Box.createVerticalStrut(20));

        // Search row
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setBackground(BG_DARK);
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        cityField = new JTextField();
        cityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cityField.setBackground(CARD_BG);
        cityField.setForeground(TEXT_WHITE);
        cityField.setCaretColor(ACCENT);
        cityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                new EmptyBorder(8, 14, 8, 14)
        ));
        cityField.putClientProperty("hint", "Enter city name...");

        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.setBackground(ACCENT);
        searchBtn.setForeground(BG_DARK);
        searchBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        searchRow.add(cityField, BorderLayout.CENTER);
        searchRow.add(searchBtn, BorderLayout.EAST);

        top.add(searchRow);

        // Actions
        searchBtn.addActionListener(e -> fetchWeather());
        cityField.addActionListener(e -> fetchWeather()); // Enter key support

        return top;
    }

    // ── Result Panel: Weather Cards ──
    private JPanel buildResultPanel() {
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(BG_DARK);
        resultPanel.setBorder(new EmptyBorder(0, 30, 20, 30));

        // Placeholder
        JLabel placeholder = new JLabel("Enter a city above to get started.");
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        placeholder.setForeground(TEXT_MUTED);
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(Box.createVerticalStrut(60));
        resultPanel.add(placeholder);

        return resultPanel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.setBackground(CARD_BG);
        bar.setBorder(new EmptyBorder(6, 20, 6, 20));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_MUTED);
        bar.add(statusLabel);
        return bar;
    }

    // ── Fetch & Display ──
    private void fetchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            statusLabel.setText("Please enter a city name.");
            return;
        }

        searchBtn.setEnabled(false);
        statusLabel.setText("Fetching weather for " + city + "...");

        SwingWorker<JSONObject, Void> worker = new SwingWorker<>() {
            @Override
            protected JSONObject doInBackground() throws Exception {
                String encoded = URLEncoder.encode(city, "UTF-8");
                String urlStr = "https://api.openweathermap.org/data/2.5/weather?q="
                        + encoded + "&appid=" + apiKey + "&units=metric";
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() != 200) return null;

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();
                conn.disconnect();
                return new JSONObject(sb.toString());
            }

            @Override
            protected void done() {
                searchBtn.setEnabled(true);
                try {
                    JSONObject obj = get();
                    if (obj == null) {
                        statusLabel.setText("City not found. Try another name.");
                        return;
                    }
                    displayWeather(obj);
                    statusLabel.setText("Updated successfully.");
                } catch (Exception ex) {
                    statusLabel.setText("Connection error. Check your network.");
                }
            }
        };
        worker.execute();
    }

    private void displayWeather(JSONObject obj) {
        String name    = obj.getString("name");
        String country = obj.getJSONObject("sys").getString("country");
        double temp    = obj.getJSONObject("main").getDouble("temp");
        double feels   = obj.getJSONObject("main").getDouble("feels_like");
        int humidity   = obj.getJSONObject("main").getInt("humidity");
        double wind    = obj.getJSONObject("wind").getDouble("speed");
        String desc    = obj.getJSONArray("weather").getJSONObject(0).getString("description");
        String main    = obj.getJSONArray("weather").getJSONObject(0).getString("main");

        resultPanel.removeAll();
        resultPanel.add(Box.createVerticalStrut(10));

        // City + temp hero card
        JPanel heroCard = makeCard();
        heroCard.setLayout(new BorderLayout());

        JLabel cityLbl = new JLabel(name + ", " + country);
        cityLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cityLbl.setForeground(TEXT_WHITE);

        JLabel tempLbl = new JLabel(String.format("%.1f°C", temp));
        tempLbl.setFont(new Font("Segoe UI", Font.BOLD, 48));
        tempLbl.setForeground(ACCENT);

        JLabel descLbl = new JLabel(capitalize(desc) + "  " + weatherEmoji(main));
        descLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        descLbl.setForeground(TEXT_MUTED);

        JPanel heroText = new JPanel();
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
        heroText.setBackground(CARD_BG);
        heroText.add(cityLbl);
        heroText.add(Box.createVerticalStrut(6));
        heroText.add(tempLbl);
        heroText.add(Box.createVerticalStrut(4));
        heroText.add(descLbl);

        heroCard.add(heroText, BorderLayout.CENTER);
        resultPanel.add(heroCard);
        resultPanel.add(Box.createVerticalStrut(12));

        // Detail cards row
        JPanel detailRow = new JPanel(new GridLayout(1, 3, 12, 0));
        detailRow.setBackground(BG_DARK);
        detailRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        detailRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailRow.add(makeDetailCard("💧 Humidity", humidity + "%", SUCCESS));
        detailRow.add(makeDetailCard("🌡 Feels Like", String.format("%.1f°C", feels), ACCENT));
        detailRow.add(makeDetailCard("💨 Wind", wind + " m/s", new Color(251, 191, 36)));

        resultPanel.add(detailRow);
        resultPanel.add(Box.createVerticalStrut(10));

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    // ── Card Builders ──
    private JPanel makeCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        return card;
    }

    private JPanel makeDetailCard(String label, String value, Color valueColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 16));
        val.setForeground(valueColor);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(6));
        card.add(val);
        return card;
    }

    // ── Helpers ──
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String weatherEmoji(String main) {
        return switch (main.toLowerCase()) {
            case "clear"        -> "☀️";
            case "clouds"       -> "☁️";
            case "rain"         -> "🌧️";
            case "drizzle"      -> "🌦️";
            case "thunderstorm" -> "⛈️";
            case "snow"         -> "❄️";
            case "mist", "fog", "haze" -> "🌫️";
            default             -> "🌈";
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WeatherApp::new);
    }
}