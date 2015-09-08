(ns lol-achievements.common.util
  (:require [clojure.core.async :refer [chan go timeout <!! >!! <! >!
                                        dropping-buffer close!]]))

(defn throttler [delay-ms]
  "Returns a function decorator that will enforce a minimum delay of
  delay-ms milliseconds between the calls of all decorated functions.

  Example usage:

    (let [func (fn [] (println \"Hello World!\"))
        func-throttler (throttler 1000)
        throttled (func-throttler func)]
    (dotimes [_ 10] (throttled)))"
  ;; Define a throttle-channel to handle cross-thread throttling.
  (let [throttle-chan (chan 1)]
    ;; Create a coroutine that will add a new value to the
    ;; throttle-channel at the rate determined by delay-ms.
    (go (while true
          ;; Note: >! will park until the value is taken off the
          ;; channel, so the coroutine won't be running unnecessarily
          ;; if the throttled functions are not being called.
          (>! throttle-chan :value)
          (<! (timeout delay-ms))))
    ;; Close the throttle-channel when the application stops.
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. #(close! throttle-chan)))
    ;; Return a decorator that will force the decorated function to
    ;; wait to take a value from the throttle-channel before executing.
    (fn [func]
      (fn [& args]
        (<!! throttle-chan)
        (apply func args)))))
