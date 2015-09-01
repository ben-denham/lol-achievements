(ns lol-achievements.common.util)

;; Decorator to cache the value returned by func in cache-atom.
(defn wrap-cache [cache-atom func]
  (fn [& args]
    (if-let [cached @cache-atom]
      cached
      (let [result (apply func args)]
        (reset! cache-atom result)
        result))))

;; Decrement that stops at zero.
(defn dec-to-zero [value]
  (if (<= value 0)
    value
    (dec value)))
