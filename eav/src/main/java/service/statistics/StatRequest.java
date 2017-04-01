package service.statistics;

/**
 * Created by Hroniko on 30.03.2017.
 */
// Класс, соотвествующий AJAX-запросу на поиск тега
public class StatRequest {

    private String plotview; // Вид диаграммы: plot - график | round - круговая диаграмма
    private String datatype; // Тип данных для диаграммы: activity - активность юзера за период | meeting - соотношение встреч | message - соотношение сообщений ...
    private String period; // Период выборки: hour - за последний час | day - за последний день | week - за последнюю неделю | month - за последний месяц | year - за последний год

    public StatRequest() {
    }

    public String getPlotview() {
        return plotview;
    }

    public void setPlotview(String plotview) {
        this.plotview = plotview;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}