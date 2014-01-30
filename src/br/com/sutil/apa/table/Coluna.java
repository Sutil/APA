package br.com.sutil.apa.table;

public class Coluna {

	private String nome;
	private String tipo;
	private String opcao;

	public Coluna(String nome, String tipo, String opcao) {
		this.nome = nome;
		this.tipo = tipo;
		this.opcao = opcao;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s %s ", nome, tipo, opcao);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getOpcao() {
		return opcao;
	}

	public void setOpcao(String opcao) {
		this.opcao = opcao;
	}
	
	

}
