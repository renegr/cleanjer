(ns csv-stat.analyzer
  (:require [clojure.data.csv :as csv]
	    [clojure.java.io :as io]))

(defn- deep-merge-with
  "Copied here from clojure.contrib.map-utils. The original may have
   been a casualty of the clojure.contrib cataclysm.

   Like merge-with, but merges maps recursively, applying the given fn
   only when there's a non-map at a particular level.

   (deepmerge + {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
                {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}})
   -> {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}"
  [f & maps]
  (apply
    (fn m [& maps]
      (if (every? map? maps)
        (apply merge-with m maps)
        (apply f maps)))
    maps))

(defn- bool-to-binary [pred] (if pred 1 0))

(defn levenshtein-distance
"
http://en.wikipedia.org/wiki/Levenshtein_distance

internal representation is a table d with m+1 rows and n+1 columns

where m is the length of a and m is the length of b.

In information theory and computer science, the Levenshtein distance is a metric for measuring the amount of difference between two sequences (i.e., the so called edit distance). The Levenshtein distance between two strings is given by the minimum number of operations needed to transform one string into the other, where an operation is an insertion, deletion, or substitution of a single character.

For example, the Levenshtein distance between \"kitten\" and \"sitting\" is 3, since the following three edits change one into the other, and there is no way to do it with fewer than three edits:

   1. kitten → sitten (substitution of 's' for 'k')
   2. sitten → sittin (substitution of 'i' for 'e')
   3. sittin → sitting (insert 'g' at the end).

The Levenshtein distance has several simple upper and lower bounds that are useful in applications which compute many of them and compare them. These include:

    * It is always at least the difference of the sizes of the two strings.
    * It is at most the length of the longer string.
    * It is zero if and only if the strings are identical.
    * If the strings are the same size, the Hamming distance is an upper bound on the Levenshtein distance.

"
  [a b]
  (let [m (count a)
        n (count b)
        init (apply deep-merge-with (fn [a b] b)
                    (concat 
                     ;;deletion
                     (for [i (range 0 (inc m))]
                       {i {0 i}})
                     ;;insertion
                     (for [j (range 0 (inc n))]
                       {0 {j j}})))
        table (reduce
               (fn [d [i j]]
                 (deep-merge-with 
                  (fn [a b] b) 
                  d 
                  {i {j (if (= (nth a (dec i))
                               (nth b (dec j)))
                          ((d (dec i)) (dec j))
                          (min 
                           (+ ((d (dec i))
                               j) 1) ;;deletion
                           (+ ((d i) 
                               (dec j)) 1) ;;insertion
                           (+ ((d (dec i))
                               (dec j)) 1))) ;;substitution
                      }}))
               init
               (for [j (range 1 (inc n))
                     i (range 1 (inc m))] [i j]))]

    ((table m) n)))



; from: http://www.learningclojure.com/2010/11/levenshtein-distance-edit-distance.html
;(defn levenshtein-distance
;  "Calculates the edit-distance between two sequences"
;  [seq1 seq2]
;  (cond
;   (empty? seq1) (count seq2)
;   (empty? seq2) (count seq1)
;   :else (min
;          (+ (if (= (first seq1) (first seq2)) 0 1)
;             (#'levenshtein-distance (rest seq1) (rest seq2))) 
;          (inc (#'levenshtein-distance (rest seq1) seq2))      
;          (inc (#'levenshtein-distance seq1 (rest seq2))))))

(defn similarities
" Calculate similarities for a given word
  within the given dictionary.
  The lower the distance, the more similarity between the two words.
  Returns a Hash Map {word distance}."
  [w dict]
  (dissoc 
   (apply hash-map
         (apply concat
                (pmap #(vector % (levenshtein-distance % w)) dict)))
   w))

(defn header
  "Returns the Header of csv-data"
  [csv-data]
  (first csv-data))

(defn body
  "Returns the body of csv-data"
  [csv-data]
  (rest csv-data))

(defn find-pos
  "Finds the position of given f in xs."
  [f xs]
  (.indexOf xs f))

(defn column-data
  "Collect all data of a given column in a seq."
  [csv-seq col-num]
  (reduce #(conj % (%2 col-num)) [] csv-seq))

(defn uniq
  "Returns all distinct values in a seq."
  [v]
  (into #{} v))

(defn load-data
     "Loads a csv file with given filename into a csv-seq."
     [file-name]
     (csv/read-csv (io/reader file-name)))