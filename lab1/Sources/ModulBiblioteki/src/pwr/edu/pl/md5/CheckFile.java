package pwr.edu.pl.md5;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CheckFile {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        HashMap<String, String> snapshot = new HashMap<String, String>();
        HashMap<String, String> currentHashMap = new HashMap<String, String>();

        try {
            snapshot = generateMd5HashMap("D:/test_java");
            writeToFile(snapshot, "test_java");
        } catch (IOException e) {
            System.out.println("err");
        }

        System.out.println(compare(snapshot, currentHashMap));
    }

   //stworzneie skrotu MD5 dla plikow o podanej sciezce
    public static HashMap<String, String> generateMd5HashMap(String absoluteDirectoryPath) throws NoSuchAlgorithmException, IOException{

        HashMap<String, String> md5HashMap = new HashMap<>();

        MessageDigest md = MessageDigest.getInstance("MD5");

        Set<String> set = createSetOfFiles(absoluteDirectoryPath);

        for (String fileName : set) {
            InputStream is = Files.newInputStream(Paths.get(absoluteDirectoryPath + "/" + fileName));
            byte[] digest = md.digest(is.readAllBytes());
            String md5Hex = convertByteToHex(digest);
            md5HashMap.put(fileName, md5Hex);
        }

        return md5HashMap;
    }


     // Zwraca zbior wszystkich plikow znajdujacych sie w danym katalogu
    public static Set<String> createSetOfFiles(String dir) throws IOException {
        Set<String> fileNameSet=Files.list(Paths.get(dir))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toSet());

        return fileNameSet;
    }


     // Zwraca skrot MD5 w postaci heksadecymalnej
    public static String convertByteToHex(byte[] digest) {

        StringBuilder sBuilder = new StringBuilder();

        for (int i = 0; i < digest.length; i++) {

            String hexString = Integer.toHexString(0xFF & digest[i]);

            sBuilder.append(hexString);
        }

        return sBuilder.toString();
    }


     // Zapisuje hashmape zawierajaca nazwe pliku oraz md5 do pliku txt
    public static void writeToFile(HashMap<String, String> map, String directoryName) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(directoryName + ".txt"));

        //entry zawiera pary klucz-wartosc
        for(Map.Entry<String, String> entry : map.entrySet()){
            //klucz i wartosc rozdzielone znakiem "--"
            bw.write(entry.getKey() + "--" + entry.getValue());
            bw.newLine();
        }

        bw.close();
    }

    /*
     * Odczytuje zapisana w pliku .txt hashmape dla danego katalogu na podstawie jego nazwy
     */
    public static HashMap<String, String> readFromFile(String directoryName) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(directoryName + ".txt"));
        HashMap<String, String> map = new HashMap<String, String>();
        String line;

        while((line = br.readLine())!= null){
            String[] keyAndValue = line.split("--");
            map.put(keyAndValue[0], keyAndValue[1]);
        }

        br.close();
        return map;
    }

    /*
     * Porownuje 2 hashmapy
     * Zwraca hashmape: klucz - nazwa pliku, wartosc - true/false
     * true - zmodyfikowano, false - brak modyfikacji
     */
    public static HashMap<String, Boolean> compare(HashMap<String, String> savedMd5HashMap, HashMap<String, String> currentMd5HashMap) {
        HashMap<String, Boolean> wasFileChangedHashMap = new HashMap<>();


        for (Map.Entry<String, String> entry : savedMd5HashMap.entrySet()) {
            String savedMD5 = entry.getValue();
            String currentMD5 = currentMd5HashMap.get(entry.getKey());

            if (savedMD5.equals(currentMD5))
                wasFileChangedHashMap.put(entry.getKey(), false);
            else
                wasFileChangedHashMap.put(entry.getKey(), true);
        }

        return wasFileChangedHashMap;
    }
}