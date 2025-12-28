package org.example.bngsocial.Utils;

import org.example.bngsocial.Models.User;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TxtManager {

    // --- 1. KULLANICI LİSTESİ ---
    public static List<User> txtUsers = new ArrayList<>();

    // --- 2. İLİŞKİ MATRİSİ (int[][]) ---
    // Binary Search ve puan hesaplaması için gerekli
    public static int[][] relationMatrix;

    // --- 3. ETKİLEŞİM VERİLERİ (Paralel Listeler - Sınıf Yok!) ---
    // Interaction sınıfı yerine verileri ayrı listelerde tutuyoruz
    public static ArrayList<String> intPostIds = new ArrayList<>();   // Paylaşım ID'leri
    public static ArrayList<Integer> intUserIds = new ArrayList<>();  // Yapan Kişi ID
    public static ArrayList<Integer> intTypes = new ArrayList<>();    // Türü (0, 1, 2)
    public static ArrayList<String> intCategories = new ArrayList<>();// "LIKE" veya "COMMENT"

    // --- BAŞLATMA METODU ---
    public static void init() {
        dosyaOku();
        //bubbleSortUsersById(); // Binary Search için sıralama ŞART

        // Matris boyutunu ayarla
        if (!txtUsers.isEmpty()) {
            relationMatrix = new int[txtUsers.size()][txtUsers.size()];
            loadRelations();
        }

        // Etkileşim listelerini temizle ve yükle
        intPostIds.clear(); intUserIds.clear(); intTypes.clear(); intCategories.clear();
        loadLikes();
        loadComments();
    }

    // --- DOSYA OKUMA: Kisiler.txt ---
    private static void dosyaOku() {
        txtUsers.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("TXT/Kisiler.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if(line.isEmpty()) continue;

                // Format: 101,Tarkan,E
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        String gender = parts.length > 2 ? parts[2].trim() : "-";
                        txtUsers.add(new User(id, name, gender));
                    } catch (NumberFormatException e) {
                        // Başlık satırıysa atla
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- DOSYA OKUMA: Iliski.txt ---
    private static void loadRelations() {
        try (BufferedReader br = new BufferedReader(new FileReader("TXT/Iliski.txt"))) {
            String line;
            int row = 0;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null && row < txtUsers.size()) {
                String[] cols = line.trim().split("[,\\s]+");

                // Başlık satırı kontrolü (Harf içeriyorsa veya ID ise atla)
                if (!headerSkipped && cols.length > 0 && cols[0].length() > 2) {
                    headerSkipped = true;
                    continue;
                }

                int colIndex = 0;
                for (String col : cols) {
                    if (colIndex >= txtUsers.size()) break;

                    if (col.equals("-")) {
                        // "-" değerini atla ama colIndex'i artır
                        relationMatrix[row][colIndex] = -1; // veya 0, tercihine bağlı
                        colIndex++;
                    }
                    // Sadece 0, 1, 2 rakamlarını al
                    else if (col.matches("[0-2]")) {
                        relationMatrix[row][colIndex] = Integer.parseInt(col);
                        colIndex++;
                    }
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- DOSYA OKUMA: Begeni.txt ---
    private static void loadLikes() {
        try (BufferedReader br = new BufferedReader(new FileReader("TXT/Begeni.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().contains("paylasimid")) continue;

                String[] p = line.split(",");
                if (p.length == 3) {
                    // Listelere Ekle
                    intPostIds.add(p[0].trim());
                    intUserIds.add(Integer.parseInt(p[1].trim()));
                    intTypes.add(Integer.parseInt(p[2].trim()));
                    intCategories.add("LIKE");
                }
            }
        } catch (Exception e) {}
    }

    // --- DOSYA OKUMA: Yorum.txt ---
    private static void loadComments() {
        try (BufferedReader br = new BufferedReader(new FileReader("TXT/Yorum.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().contains("paylasimid")) continue;

                String[] p = line.split(",");
                if (p.length == 3) {
                    // Listelere Ekle
                    intPostIds.add(p[0].trim());
                    intUserIds.add(Integer.parseInt(p[1].trim()));
                    intTypes.add(Integer.parseInt(p[2].trim()));
                    intCategories.add("COMMENT");
                }
            }
        } catch (Exception e) {}
    }

    // --- SIRALAMA (Bubble Sort) ---
    private static void bubbleSortUsersById() {
        int n = txtUsers.size();
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (txtUsers.get(j).getId() > txtUsers.get(j + 1).getId()) {
                    User temp = txtUsers.get(j);
                    txtUsers.set(j, txtUsers.get(j + 1));
                    txtUsers.set(j + 1, temp);
                }
    }

    // --- BINARY SEARCH ---
    public static User findUserByIdBinary(int targetId) {
        int low = 0;
        int high = txtUsers.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int midId = txtUsers.get(mid).getId();

            if (midId == targetId) return txtUsers.get(mid);
            if (midId < targetId) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }

    public static int getUserIndex(int id) {
        User u = findUserByIdBinary(id);
        if (u == null) return -1;
        return txtUsers.indexOf(u);
    }

    // --- ARKADAŞ MI KONTROLÜ ---
    public static String arkadasMi(int id1, int id2) {
        int idx1 = getUserIndex(id1);
        int idx2 = getUserIndex(id2);

        if (idx1 == -1 || idx2 == -1) return "Kullanıcı Bulunamadı";

        int status = relationMatrix[idx1][idx2];
        if (status == 1) return "Arkadaş";
        if (status == 2) return "Yakın Arkadaş";
        return "Arkadaş Değil";
    }

    // --- İLİŞKİ PUANI HESAPLA ---
    public static double iliskiPuaniHesapla(int sourceId, int targetId) {
        int idx1 = getUserIndex(sourceId);
        int idx2 = getUserIndex(targetId);

        if (idx1 == -1 || idx2 == -1) return 0;

        double score = 0;

        // 1. İlişki Durumu Puanı
        int status = relationMatrix[idx1][idx2];
        if (status == 1) score += 15;
        else if (status == 2) score += 30;

        // 2. Etkileşim Puanları (Paralel Listeleri Geziyoruz)
        String targetPrefix = targetId + "-";
        String targetPrefixAlt = targetId + "P";

        for (int i = 0; i < intPostIds.size(); i++) {
            // Yapan kişi (source) mu?
            if (intUserIds.get(i) == sourceId) {
                String pId = intPostIds.get(i);

                // Post hedef kişiye (target) mi ait?
                if (pId.startsWith(targetPrefix) || pId.startsWith(targetPrefixAlt)) {
                    int type = intTypes.get(i);
                    String cat = intCategories.get(i);

                    if (cat.equals("LIKE")) {
                        // Dosya: 1=Beğenme (+5), 0=Beğenmeme (-5)
                        if (type == 1) score += 5;
                        else if (type == 0) score -= 5;
                    }
                    else if (cat.equals("COMMENT")) {
                        // Dosya: 1=Olumlu (+10), 0=Nötr (+5), 2=Olumsuz (-5)
                        if (type == 1) score += 10;
                        else if (type == 0) score += 5;
                        else if (type == 2) score -= 5;
                    }
                }
            }
        }
        return score;
    }

    // --- ÖNERİ PUANI ---
    public static double oneriPuaniHesapla(int candidateId, int targetId) {
        double baseScore = iliskiPuaniHesapla(candidateId, targetId);
        double friendsScoreSum = 0;

        int targetIdx = getUserIndex(targetId);

        // Hedefin arkadaşlarını bul ve puanları topla
        for (int i = 0; i < txtUsers.size(); i++) {
            if (relationMatrix[targetIdx][i] > 0) { // Arkadaşsa
                int friendId = txtUsers.get(i).getId();
                friendsScoreSum += iliskiPuaniHesapla(candidateId, friendId);
            }
        }
        return baseScore + friendsScoreSum;
    }

    // --- LİSTELEME YARDIMCILARI ---

    // Arkadaş Öner (Max 3)
    public static List<String> arkadasOner(int userId) {
        List<User> candidates = new ArrayList<>();
        List<Double> scores = new ArrayList<>();

        int userIdx = getUserIndex(userId);
        if(userIdx == -1) return new ArrayList<>();

        for (int i = 0; i < txtUsers.size(); i++) {
            // Arkadaş değilse
            if (i != userIdx && relationMatrix[userIdx][i] == 0) {
                double s = oneriPuaniHesapla(txtUsers.get(i).getId(), userId);
                candidates.add(txtUsers.get(i));
                scores.add(s);
            }
        }

        sortListsDesc(candidates, scores); // Puanı yüksek olan başa
        return formatResult(candidates, scores, 3);
    }

    // Arkadaş Çıkarma Öner (Max 3)
    public static List<String> arkadasCikarmaOner(int userId) {
        List<User> candidates = new ArrayList<>();
        List<Double> scores = new ArrayList<>();

        int userIdx = getUserIndex(userId);
        if(userIdx == -1) return new ArrayList<>();

        for (int i = 0; i < txtUsers.size(); i++) {
            // Arkadaşsa
            if (relationMatrix[userIdx][i] > 0) {
                double s = iliskiPuaniHesapla(txtUsers.get(i).getId(), userId);
                candidates.add(txtUsers.get(i));
                scores.add(s);
            }
        }

        sortListsAsc(candidates, scores); // Puanı düşük olan başa
        return formatResult(candidates, scores, 3);
    }

    // En Çok Beğeni/Yorum Alanlar (Grafik/Enler için)
    public static List<String> enCokEtkilesimAlan(String category) {
        // Her user için sayaç (index bazlı)
        int[] counts = new int[txtUsers.size()];

        for (int i = 0; i < intPostIds.size(); i++) {
            if (intCategories.get(i).equals(category)) {
                // Post sahibini bul: "107-P01" -> 107
                try {
                    String pId = intPostIds.get(i);
                    String ownerIdStr = pId.split("-|P")[0];
                    int ownerId = Integer.parseInt(ownerIdStr);
                    int idx = getUserIndex(ownerId);
                    if (idx != -1) counts[idx]++;
                } catch(Exception e) {}
            }
        }

        // Sıralama için geçici listeler
        List<User> uList = new ArrayList<>(txtUsers);
        List<Double> cList = new ArrayList<>();
        for(int c : counts) cList.add((double)c);

        sortListsDesc(uList, cList);
        return formatResult(uList, cList, 3);
    }

    // --- YARDIMCI METOTLAR ---

    private static void sortListsDesc(List<User> uList, List<Double> sList) {
        for (int i = 0; i < sList.size() - 1; i++) {
            for (int j = 0; j < sList.size() - i - 1; j++) {
                if (sList.get(j) < sList.get(j + 1)) {
                    // Swap Puan
                    double tempS = sList.get(j); sList.set(j, sList.get(j + 1)); sList.set(j + 1, tempS);
                    // Swap User
                    User tempU = uList.get(j); uList.set(j, uList.get(j + 1)); uList.set(j + 1, tempU);
                }
            }
        }
    }

    private static void sortListsAsc(List<User> uList, List<Double> sList) {
        for (int i = 0; i < sList.size() - 1; i++) {
            for (int j = 0; j < sList.size() - i - 1; j++) {
                if (sList.get(j) > sList.get(j + 1)) {
                    // Swap
                    double tempS = sList.get(j); sList.set(j, sList.get(j + 1)); sList.set(j + 1, tempS);
                    User tempU = uList.get(j); uList.set(j, uList.get(j + 1)); uList.set(j + 1, tempU);
                }
            }
        }
    }

    private static List<String> formatResult(List<User> uList, List<Double> sList, int limit) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, uList.size()); i++) {
            res.add(uList.get(i).getName() + " (" + sList.get(i).intValue() + ")");
        }
        return res;
    }
}