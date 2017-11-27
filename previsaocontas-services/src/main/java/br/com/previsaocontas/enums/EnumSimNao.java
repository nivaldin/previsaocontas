package br.com.previsaocontas.enums;

public enum EnumSimNao {
	S("Sim"), N("Não");

	private final String valor;

	public String getValor() {
		return valor;
	}

	EnumSimNao(String valorOpcao) {
		valor = valorOpcao;
	}

}
