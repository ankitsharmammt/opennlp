/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreemnets.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package opennlp.tools.postag;

import java.util.Iterator;

import opennlp.tools.util.Mean;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.ObjectStreamException;

/**
 * The {@link POSEvaluator} measures the performance of
 * the given {@link POSTagger} with the provided reference
 * {@link POSSamplee}s.
 */
public class POSEvaluator {

  private POSTagger tagger;

  private Mean wordAccuracy = new Mean();

  /**
   * Initializes the current instance.
   *
   * @param tagger
   */
  public POSEvaluator(POSTagger tagger) {
    this.tagger = tagger;
  }

  /**
   * Evaluates the given reference {@link POSSample} object.
   *
   * This is done by tagging the sentence from the reference
   * {@link POSSample} with the {@link POSTagger}. The
   * tags are then used to update the word accuracy score.
   *
   * @param reference the reference {@link POSSample}.
   */
  public void evaluateSample(POSSample reference) {

    String predictedTags[] = tagger.tag(reference.getSentence());

    for (int i = 0; i < reference.getTags().length; i++) {
      if (reference.getTags()[i].equals(predictedTags[i])) {
        wordAccuracy.add(1);
      }
      else {
        wordAccuracy.add(0);
      }
    }
  }

  /**
   * Reads all {@link POSSample} objects from the stream
   * and evaluates each {@link POSSample} object with
   * {@link #evaluateSample(POSSample)} method.
   *
   * @param samples the stream of reference {@link POSSample} which
   * should be evaluated.
   */
  public void evaluate(ObjectStream<POSSample> samples) throws ObjectStreamException {
    POSSample sample;
    while ((sample = samples.read()) != null) {
      evaluateSample(sample);
    }
  }

  /**
   * Retrieves the word accuracy.
   *
   * This is defined as:
   * word accuracy = correctly detected tags / total words
   *
   * @return the word accuracy
   */
  public double getWordAccuracy() {
    return wordAccuracy.mean();
  }

  /**
   * Retrieves the total number of words considered
   * in the evaluation.
   * 
   * @return
   */
  public long getWordCount() {
    return wordAccuracy.count();
  }
  
  /**
   * Represents this objects as human readable {@link String}.
   */
  public String toString() {
    return "Accuracy:" + wordAccuracy.mean() +
        " Number of Samples: " + wordAccuracy.count();
  }
}
