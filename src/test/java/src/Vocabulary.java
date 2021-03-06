package src;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.hadoop.io.Writable;

import java.io.*;
import java.util.Map;

public class Vocabulary implements Writable {
    private Map<String, Integer> wordIds;
    private Map<String, Integer> idf;

    Vocabulary() {
    }

    Vocabulary(Map<String, Integer> wordIds, Map<String, Integer> idf) {
        this.wordIds = wordIds;
        this.idf = idf;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Gson gson = new Gson();
        dataOutput.writeChars(gson.toJson(wordIds));
        dataOutput.writeChars(gson.toJson(idf));
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        String serializedWordIds = dataInput.readLine();
        Gson gson = new Gson();
        wordIds = gson.fromJson(serializedWordIds, new TypeToken<Map<String, Integer>>() {
        }.getType());
        String serializedIdf = dataInput.readLine();
        idf = gson.fromJson(serializedIdf, new TypeToken<Map<String, Integer>>() {
        }.getType());
    }

    public Map<String, Integer> getWordIds() {
        return wordIds;
    }

    public void setWordIds(Map<String, Integer> wordIds) {
        this.wordIds = wordIds;
    }

    public Map<String, Integer> getIdf() {
        return idf;
    }

    public void setIdf(Map<String, Integer> idf) {
        this.idf = idf;
    }

    public static Vocabulary readVocabulary(String fileName) throws IOException {
        Vocabulary vocabulary = new Vocabulary();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
            Gson gson = new Gson();
            String serializedWordIds = bufferedReader.readLine();
            vocabulary.setWordIds(gson.fromJson(serializedWordIds, new TypeToken<Map<String, Integer>>() {}.getType()));
            String serializedIdf = bufferedReader.readLine();
            vocabulary.setIdf(gson.fromJson(serializedIdf, new TypeToken<Map<String, Integer>>() {}.getType()));
        }

        return vocabulary;
    }

    public static void writeVocabularyToFile(Vocabulary vocabulary, String fileName) throws IOException {
        try (PrintWriter printWriter = new PrintWriter(new File(fileName))) {
            Gson gson = new Gson();
            printWriter.println(gson.toJson(vocabulary.getWordIds()));
            printWriter.println(gson.toJson(vocabulary.getIdf()));
        }
    }
}
