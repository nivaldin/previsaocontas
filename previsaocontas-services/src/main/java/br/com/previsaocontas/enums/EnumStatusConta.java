package br.com.previsaocontas.enums;

public enum EnumStatusConta {

	A("Aberto"), B("Baixado"), P("Parcial");
	
	private final String valor;

	public String getValor() {
		return valor;
	}
	
	private EnumStatusConta(String valorOpcao) {
		this.valor = valorOpcao;
	}
	
}
