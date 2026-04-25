package com.ibby.hub.home.split;

import com.ibby.hub.home.split.util.Diff;
import com.ibby.hub.home.split.util.KeyValue;
import com.ibby.hub.home.split.util.Total;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Split {
  private final String filename;
  private final String PERSON_1;
  private final String PERSON_2;
  private final Map<String, KeyValue> count = new HashMap<>();
  private final Set<String> typeSet = new HashSet<>();

  public Split(String filename, String person1, String person2) {
    this.filename = filename;
    this.PERSON_1 = person1;
    this.PERSON_2 = person2;
  }

  public void readFile() throws IOException {
    String downloadsDir = System.getProperty("user.home") + File.separator + "Downloads";
    File baseDir = new File(downloadsDir);
    File targetFile = new File(baseDir, filename).getCanonicalFile();
    if (!targetFile.getPath().startsWith(baseDir.getCanonicalPath())) {
      throw new SecurityException("Invalid file path");
    }
    try (FileInputStream fis = new FileInputStream(targetFile); ZipInputStream zipInputStream = new ZipInputStream(fis)) {
      ZipEntry entry;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          String fileContent = readFileContent(zipInputStream);
          String[] split = fileContent.split("\n");
          mapCountMap(split);
          Total total = calculateTotal(split);
          System.out.printf("Total %s: %.2f%n", PERSON_1, total.person1());
          System.out.printf("Total %s: %.2f%n", PERSON_2, total.person2());
          Diff diff = calculateDiff(total);
          System.out.printf("%s schuldet: %.2f%n", diff.name(), diff.sum());
          System.out.printf("%s%n", "-".repeat(30));
          count.values().forEach(k -> System.out.printf("%s:\t%s:%.2f\t|\t%s:%.2f%n", k.type(), PERSON_1, k.total().person1(), PERSON_2, k.total().person2()));
        }
        zipInputStream.closeEntry();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String readFileContent(ZipInputStream zipInputStream) throws IOException {
    StringWriter stringWriter = new StringWriter();
    char[] buffer = new char[1024];
    int bytesRead;
    InputStreamReader reader = new InputStreamReader(zipInputStream, StandardCharsets.UTF_8);
    while ((bytesRead = reader.read(buffer)) != -1) {
      stringWriter.write(buffer, 0, bytesRead);
    }
    return stringWriter.toString();
  }

  private Total calculateTotal(String[] split) {
    double totalPerson1 = 0;
    double totalPerson2 = 0;
    for (int i = 4; i < split.length; i++) {
      String substring = split[i].substring(17).trim();
      if (substring.contains("<") || !substring.contains(",")) {
        continue;
      }
      String[] whoAndWhat = substring.split(":");
      String[] sumAndWhat = whoAndWhat[1].split(",");
      double sum = Double.parseDouble(sumAndWhat[0]);
      String who = whoAndWhat[0].toLowerCase();
      updateCountMap(sumAndWhat[1].trim(), who, sum);
      if (who.contains(PERSON_1.toLowerCase())) {
        totalPerson1 += sum;
      } else {
        totalPerson2 += sum;
      }
    }
    return new Total(totalPerson1, totalPerson2);
  }

  private void mapCountMap(String[] split) {
    Arrays.stream(split).filter(line -> line.contains("=")).forEach(e -> {
      String trim = e.substring(17).trim().split(":")[1].trim();
      String[] kv = trim.split("=");
      String value = kv[1].trim();
      if (kv[1].contains("<")) {
        value = kv[1].replace(" <Diese Nachricht wurde bearbeitet.>", "");
      }
      count.put(kv[0], new KeyValue(value, new Total()));
      typeSet.add(kv[0].toLowerCase());
    });
  }

  private void updateCountMap(String key, String who, double sum) {
    if (count.containsKey(key)) {
      count.put(key, updateKeyValue(count.get(key), who, sum));
    }
    if (typeSet.contains(key.toLowerCase())) {
      count.entrySet()
        .stream()
        .filter(e -> e.getValue().type().equals(key))
        .map(Map.Entry::getKey)
        .findFirst().ifPresent(foundKey -> count.put(foundKey, updateKeyValue(count.get(foundKey), who, sum)));
    }
  }

  private Diff calculateDiff(Total total) {
    double diff;
    String name;
    if (total.person1() > total.person2()) {
      diff = (total.person1() - total.person2()) / 2;
      name = PERSON_2;
    } else {
      diff = (total.person2() - total.person1()) / 2;
      name = PERSON_1;
    }
    return new Diff(diff, name);
  }

  private KeyValue updateKeyValue(KeyValue value, String who, double sum) {
    double person1 = value.total().person1();
    double person2 = value.total().person2();
    if (who.contains(PERSON_1)) {
      person1 += sum;
    } else {
      person2 += sum;
    }
    return new KeyValue(value.type(), new Total(person1, person2));
  }
}

