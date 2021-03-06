import com.google.gson.Gson;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Document implements WritableComparable<Document> {

    private int id;
    private String title = "";
    private String url = "";
    private String text = "";
    private double relevance = 0;

    Document() {
    }

    Document(String serialized) {
        Gson gson = new Gson();
        Document document = gson.fromJson(serialized, Document.class);
        id = document.id;
        title = document.title;
        text = document.text;
        url = document.url;
        relevance = document.relevance;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Gson gson = new Gson();
        dataOutput.writeChars(gson.toJson(this));
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        Gson gson = new Gson();
        Document document = gson.fromJson(dataInput.readLine(), Document.class);
        id = document.id;
        title = document.title;
        text = document.text;
        url = document.url;
        relevance = document.relevance;
    }

    @Override
    public int compareTo(Document o) {
        return Double.compare(o.getRelevance(), relevance);
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }
}
