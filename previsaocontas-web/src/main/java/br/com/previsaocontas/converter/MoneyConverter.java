package br.com.previsaocontas.converter;

import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "moneyConverter")
public class MoneyConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
		if (value == null) {
			return "";
		}
		return value.replace(".", "").replace(",", ".");
	
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {

		if (value == null) {
			return "";
		}
		if (value instanceof String) {
			return (String) value;
		}
		try {
			DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
			return formatter.format(value);

		} catch (Exception e) {
			throw new ConverterException("Formato n�o � n�mero.");
		}
		
	}

}
