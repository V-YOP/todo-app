package me.yuuki.todoapp.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TaskComparator implements Comparator<Task> {

    public static TaskComparator get() {
        return new TaskComparator();
    }

    /**
     * 首先比较是否为同一个Task
     * <p>
     * 如果不为同一个Task，则未完成的Task更前
     * <p>
     * 如果完成情况相同，优先级越高的更前
     * <p>
     * 如果优先级相同，开始和结束日期差距越小的越前
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     */
    @Override
    public int compare(Task o1, Task o2) {
        if (o1 == o2)
            return 0;
        if (o1.getDone() != o2.getDone())
            return -o1.getDone().compareTo(o2.getDone());
        if (o1.getPriority() != o2.getPriority())
            return o1.getPriority().compareTo(o2.getPriority());
        int days1 = getIntervalDays(o1);
        int days2 = getIntervalDays(o2);
        if (days1 != days2)
            return days2 - days1;

        return 0;
    }

    private int getIntervalDays(Task task) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = task.getStartDate().orElse(dateFormat.parse("1970-01-01"));
            Date endDate   = task.getEndDate().orElse(dateFormat.parse("2099-12-31"));
            return getIntervalDays(startDate, endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取两个时间相差的天数
     */
    private int getIntervalDays(Date startTime, Date endTime) {
        long et = endTime.getTime();
        long st = startTime.getTime();
        long l = et - st;
        long dayCount = l / (24 * 60 * 60 * 1000);
        return new Integer((int) (dayCount + 1));
    }
}
