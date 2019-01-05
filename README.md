# Machine Learning WS 18/19
# Gruppe: Johanna Seibert, Benedict Steffens, David Koch, Elisa Hildebrandt, Nicolas Morel
# ID3 Implementation

- `Main.java` will execute the ID3 Algorithm for the given CSV-File (given as system argument) and prints the resulting DecisionTree to the console. Additionally, it will create a .dot in the project folder which can be converted to a .png by using https://www.graphviz.org/
- You can change the target attribute for classification in the second parameter of `runAlgorithm()` call.

# DecisionTree for mushrooms.csv :

   odor -->
     p
        = p
     a
        = e
     c
        = p
     s
        = p
     f
        = p
     y
        = p
     l
        = e
     m
        = p
     n
       spore-print-color -->
         r
            = p
         b
            = e
         w
           habitat -->
             p
                = e
             d
               gill-size -->
                 b
                    = e
                 n
                    = p
             w
                = e
             g
                = e
             l
               cap-color -->
                 c
                    = e
                 w
                    = p
                 y
                    = p
                 n
                    = e
         h
            = e
         y
            = e
         k
            = e
         n
            = e
         o
            = e
