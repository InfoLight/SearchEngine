import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Indexer {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("Start reading vocabulary...");
        Vocabulary vocabulary = Vocabulary.readVocabulary(args[args.length - 2]);
        System.out.println("Vocabulary read ended...");

        Configuration conf = new Configuration();
        conf.set("vocabulary", vocabulary.serialize());
        Job job = Job.getInstance(conf, "indexing");
        job.setJarByClass(Indexer.class);
        job.setMapperClass(Indexer.IndexMap.class);
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(DoubleWritable.class);
        for (int i = 0; i < args.length - 2; i++) {
            MultipleInputs.addInputPath(job, new Path(args[i]), TextInputFormat.class);
        }
        FileOutputFormat.setOutputPath(job, new Path(args[args.length - 1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static class IndexMap extends Mapper<LongWritable, Text, DocVector, NullWritable> {

        private static Vocabulary vocabulary;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            vocabulary = Vocabulary.deserialize(context.getConfiguration().get("vocabulary"));
        }

        public void map(LongWritable offset, Text doc, Context context) throws IOException, InterruptedException {
            Document document = new Document(doc.toString());

            Map<Integer, Integer> wordMap = new HashMap<>();
            Map<String, Integer> wordIds = vocabulary.getWordIds();
            StringTokenizer tokens = new StringTokenizer(document.getText());

            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken().toLowerCase();
                token = token.replaceAll("[^\\w&&[^-]]", "");
                if (token.length() != 0) {
                    Integer wordId = wordIds.get(token);
                    if (!wordMap.containsKey(wordId)) {
                        wordMap.put(wordId, 1);
                    } else {
                        wordMap.put(wordId, wordMap.get(wordId) + 1);
                    }
                }
            }

            Map<Integer, Double> result = new HashMap<>();
            for (Map.Entry<String, Integer> entry : vocabulary.getIdf().entrySet()) {
                Integer wordId = vocabulary.getWordIds().get(entry.getKey());
                assert wordId != null;
                assert wordMap.get(wordId) != null;
                assert entry.getValue() != null;
                result.put(wordId, (double) (wordMap.get(wordId) / entry.getValue()));
            }
            context.write(new DocVector(document.getId(), result), NullWritable.get());

        }
    }
}
