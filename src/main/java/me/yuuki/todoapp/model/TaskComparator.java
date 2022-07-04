package me.yuuki.todoapp.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

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
     * 如果优先级相同，当前日期和结束日期差距越小越前（过期的日期最前）
     * <p>
     * 如果结束日期差距相同相同，开始时间和当前日期差距越大越前（未来的日期最后）
     * <p>
     * 如果差距相同，id越大的越前
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     */
    @Override
    public int compare(Task o1, Task o2) {
        // 一个模式是，当 o1.xxx.compareTo(o2.xxx)时是升序，否则是降序

        if (o1 == o2)
            return 0;
        if (o1.getDone() != o2.getDone())
            return o1.getDone().compareTo(o2.getDone());
        if (o1.getPriority() != o2.getPriority())
            return o2.getPriority().compareTo(o1.getPriority());

        int endDayInterval1 = getIntervalBetweenCurrentAndEndDate(o1);
        int endDayInterval2 = getIntervalBetweenCurrentAndEndDate(o2);
        if (endDayInterval1 != endDayInterval2)
            return Objects.compare(endDayInterval1, endDayInterval2, Integer::compareTo);

        int startDayInterval1 = getIntervalBetweenStartDateAndCurrent(o1);
        int startDayInterval2 = getIntervalBetweenStartDateAndCurrent(o2);
        if (startDayInterval1 != startDayInterval2)
            return Objects.compare(startDayInterval2, startDayInterval1, Integer::compareTo);

        return Objects.compare(o2.getId(), o1.getId(), Long::compareTo);
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

    private int getIntervalBetweenCurrentAndEndDate(Task task) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = dateFormat.parse(dateFormat.format(new Date()));
            Date endDate   = task.getEndDate().orElse(dateFormat.parse("2099-12-31"));
            return getIntervalDays(startDate, endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private int getIntervalBetweenStartDateAndCurrent(Task task) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = task.getStartDate().orElse(dateFormat.parse("1970-01-01"));
            Date endDate   = dateFormat.parse(dateFormat.format(new Date()));
            return getIntervalDays(startDate, endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取两个时间相差的天数
     */
    private int getIntervalDays(Date startTime, Date endTime) {
        return (int) ((endTime.getTime() - startTime.getTime()) / (24 * 60 * 60 * 1000) + 1);
    }
}
