package br.com.previsaocontas.enums;

public enum EnumTipoConta {

	D("Despesa"), R("Receita");
	
	private final String valor;
	
	public String getValor() {
		return valor;
	}

	private EnumTipoConta(String valorOpcao) {
		this.valor = valorOpcao;
	}
	
}
