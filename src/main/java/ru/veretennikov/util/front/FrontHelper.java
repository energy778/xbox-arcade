package ru.veretennikov.util.front;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.Arrays;
import java.util.Locale;

public class FrontHelper {

    public static final Locale DATE_PICKER_LOCALE = new Locale("ru");
    public static final DatePicker.DatePickerI18n DATE_PICKER_I18N = new DatePicker.DatePickerI18n()
            .setWeek("Неделя").setCalendar("Календарь").setClear("Очистить").setToday("Сегодня").setCancel("Отмена").setFirstDayOfWeek(1)
            .setMonthNames(Arrays.asList("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"))
            .setWeekdays(Arrays.asList("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"))
            .setWeekdaysShort(Arrays.asList("вс", "пн", "вт", "ср", "чт", "пт", "сб"));

}
