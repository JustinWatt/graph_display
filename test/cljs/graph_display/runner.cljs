(ns graph-display.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [graph-display.core-test]))

(doo-tests 'graph-display.core-test)
