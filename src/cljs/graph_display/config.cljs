(ns graph-display.config)

(def debug?
  ^boolean js/goog.DEBUG)

(when debug?
  (enable-console-print!))