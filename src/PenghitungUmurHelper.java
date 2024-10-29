/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.Period;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.JTextArea;
import org.json.JSONObject;
import java.io.BufferedReader; 
import java.io.InputStreamReader; 
import java.net.HttpURLConnection; 
import java.net.URL; 
import java.util.function.Supplier; 
import javax.swing.JTextArea; 
import org.json.JSONArray; 
import org.json.JSONObject; 
/**
 *
 * @author Rezqi
 */
public class PenghitungUmurHelper {
    public String hitungUmurDetail(LocalDate lahir, LocalDate sekarang) { 
    Period period = Period.between(lahir, sekarang); 
    return period.getYears() + " tahun, " + period.getMonths() + " bulan, " + period.getDays() + " hari"; 
}
    public LocalDate hariUlangTahunBerikutnya(LocalDate lahir, LocalDate 
    sekarang) { 
        LocalDate ulangTahunBerikutnya = 
        lahir.withYear(sekarang.getYear()); 
        if (!ulangTahunBerikutnya.isAfter(sekarang)) { 
        ulangTahunBerikutnya = ulangTahunBerikutnya.plusYears(1); 
} 
    return ulangTahunBerikutnya; 
} 
    public String getDayOfWeekInIndonesian(LocalDate date) { 
        switch (date.getDayOfWeek()) { 
            case MONDAY: 
            return "Senin"; 
            case TUESDAY: 
            return "Selasa"; 
            case WEDNESDAY: 
            return "Rabu"; 
            case THURSDAY: 
            return "Kamis"; 
            case FRIDAY: 
            return "Jumat"; 
            case SATURDAY: 
            return "Sabtu"; 
            case SUNDAY: 
            return "Minggu"; 
            default: 
        return ""; 
        }
    }
    
    public void getPeristiwaBarisPerBaris(LocalDate tanggal, JTextArea txtAreaPeristiwa, Supplier<Boolean> shouldStop){
        try{if (shouldStop.get()){
            return;
        }
        
        String urlString = "https://byabbe.se/on-this-day/" + tanggal.getMonthValue() + "/" + tanggal.getDayOfMonth() + "/events.json";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200){
            throw new Exception("HTTP response code: " + responseCode + ". Silahkan coba lagi nanti atau cek koneksi internet. ");
        }
        BufferedReader in = new BufferedReader (new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine())!=null){
            if(shouldStop.get()){
                in.close();
                conn.disconnect();
                javax.swing.SwingUtilities.invokeLater(() -> txtAreaPeristiwa.setText("Pengambilan data dibatalkan. \n"));
                return;
            }
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();
        
        JSONObject json = new JSONObject(content.toString());
        JSONArray events = json.getJSONArray("events");
        for (int i = 0; i < events.length();i++) {
            if (shouldStop.get()){
                javax.swing.SwingUtilities.invokeLater(() -> txtAreaPeristiwa.setText("Pengambilan data dibatalkan. \n"));
                return;
            }
            JSONObject event = events.getJSONObject(i);
            String year = event.getString("year");
            String description = event.getString("description");
            String peristiwa = year + ": " + description;
            javax.swing.SwingUtilities.invokeLater(() -> txtAreaPeristiwa.append(peristiwa + "\n"));
        }
        if (events.length() == 0){
            javax.swing.SwingUtilities.invokeLater(() -> txtAreaPeristiwa.setText("Tidak ada peristiwa penting yang ditemukan pada tanggal ini."));
        }
        } catch (Exception e) {
            javax.swing.SwingUtilities.invokeLater(() -> txtAreaPeristiwa.setText("Gagal mendapatkan data peristiwa: " + e.getMessage()));
        }
    }
}
