package web;
/**
 * Created by Hroniko on 24.02.2017.
 * Класс для хранения пересылаемых ответов на запросы в AJAX
 */
public class Response {
	
	private int count;
	private String text;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
