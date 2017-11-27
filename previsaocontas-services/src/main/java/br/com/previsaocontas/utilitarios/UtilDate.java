package br.com.previsaocontas.utilitarios;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>
 * <b>Title:</b> UtilDate
 * </p>
 * 
 * <p>
 * <b>Description:</b> Classe utilitária para trabalhar com Data.
 * </p>
 * 
 * <p>
 * <b>Company: </b> ITSS Soluções em Tecnologia
 * </p>
 * 
 * @author Silvânio Júnior
 * 
 * @version 1.0.0
 */
public class UtilDate {

	/** Atributo PATTERN. */
	private static final String	PATTERN	= "dd/MM/yyyy";

	/**
	 * Método responsável por retornar a data atual.
	 *
	 * @author Silvânio Júnior
	 *
	 * @return <code>Date</code> Data Atual.
	 */
	public static Date hoje() {

		return new Date();
	}

	/**
	 * Método responsável por verificar se é final de semana.
	 *
	 * @author Silvânio Júnior
	 *
	 * @param date
	 * 
	 * @return boolean.
	 */
	public static boolean isFinalDeSemana(final Date date) {

		final GregorianCalendar gc = new GregorianCalendar();

		gc.setTime(date);

		final int diaDaSemana = gc.get(Calendar.DAY_OF_WEEK);

		return ( diaDaSemana == 1 || diaDaSemana == 7 );

	}

	/**
	 * Método responsável por formatar data.
	 *
	 * @author Silvânio Júnior
	 *
	 * @param data
	 * 
	 * @return <code>String</code>
	 */
	public static String format(final Date data) {

		return UtilDate.format(data, UtilDate.PATTERN);
	}

	/**
	 * Método responsável por formatar data.
	 *
	 * @author Silvânio Júnior
	 *
	 * @param data
	 * 
	 * @return <code>String</code>
	 */
	public static String format(final Date data, final String pattern) {

		final SimpleDateFormat dt = new SimpleDateFormat(pattern);

		return dt.format(data);

	}
	/**
	 * 
	 * Método responsável por verificar se a data final é maior que a data inicial
	 *
	 * @author Joaquim Barros
	 *
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static boolean isDataFinalMenorQueInicial(Date dataInicial, Date dataFinal) {

		if (dataInicial.after(dataFinal)) {
			return true;
		} else
			return false;
	}
	
	public static Date addDays(Date date, int days)
	{
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.DATE, days); //minus number would decrement the days
	    return cal.getTime();
	}	
	
	public static Date addMonths(Date date, int months)
	{
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.MONTH, months); //minus number would decrement the days
	    return cal.getTime();
	}

}
