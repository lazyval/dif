package space.kostya.dif.format

import java.time.{Duration, LocalDateTime}

object HumanDates {
  // gives imprecise human readable format of time difference
  // imprecise means 65 minutes will be counted as 1 hour ago, not 1 hour and 5 minutes ago
  def approxDifference(earlier: LocalDateTime, later: LocalDateTime): String = {
    val seconds = Duration.between(earlier, later).getSeconds
    if (seconds < 60) {
      s"$seconds seconds"
    } else if (seconds < 3600) {
      val minutes = seconds / 60
      s"$minutes minutes"
    } else if (seconds < 86400) {
      val hours = seconds / 3600
      s"$hours hours"
    } else if (seconds < 604800) {
      val days = seconds / 86400
      s"$days days"
    } else if (seconds < 2629746) {
      val weeks = seconds / 604800
      s"$weeks weeks"
    } else if (seconds < 31556952) {
      val months = seconds / 2629746
      s"$months months"
    } else {
      val years = seconds / 31556952
      s"$years years"
    }
  }
}
