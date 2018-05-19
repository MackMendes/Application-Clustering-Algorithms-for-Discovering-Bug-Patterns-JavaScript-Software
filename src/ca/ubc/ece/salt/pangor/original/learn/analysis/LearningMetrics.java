package ca.ubc.ece.salt.pangor.original.learn.analysis;

import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import java.util.Comparator;
import java.util.TreeSet;

public class LearningMetrics {
    public TreeSet<KeywordFrequency> changedKeywordFrequency;
    public TreeSet<KeywordFrequency> keywordFrequency;

    public LearningMetrics() {
        Comparator<KeywordFrequency> comparator = new Comparator<KeywordFrequency>(){

            @Override
            public int compare(LearningMetrics.KeywordFrequency o1, LearningMetrics.KeywordFrequency o2) {
                if (o1.frequency > o2.frequency) {
                    return -1;
                }
                if (o1.frequency < o2.frequency) {
                    return 1;
                }
                return o1.keyword.toString().compareTo(o2.keyword.toString());
            }
        };
        this.changedKeywordFrequency = new TreeSet<KeywordFrequency>(comparator);
        this.keywordFrequency = new TreeSet<KeywordFrequency>(comparator);
    }

    public void addKeywordFrequency(KeywordUse keyword, int frequency) {
        switch (keyword.changeType) {
            case INSERTED: 
            case REMOVED: 
            case UPDATED: {
                this.changedKeywordFrequency.add(new KeywordFrequency(keyword, frequency));
            }
		default:
			this.keywordFrequency.add(new KeywordFrequency(keyword, frequency));
        }        
    }

    public class KeywordFrequency {
        public KeywordUse keyword;
        public int frequency;

        public KeywordFrequency(KeywordUse keyword, int frequency) {
            this.keyword = keyword;
            this.frequency = frequency;
        }
    }

}

