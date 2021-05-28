package com.myBank.model.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the cuenta database table.
 * 
 */
@Entity
@NamedQuery(name="Cuenta.findAll", query="SELECT c FROM Cuenta c")
public class Cuenta implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String descripcion;

	private String iban;

	private float saldo;

	//bi-directional many-to-one association to Divisa
	@ManyToOne(fetch=FetchType.LAZY)
	private Divisa divisa;

	//bi-directional many-to-one association to Usuario
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="titular")
	private Usuario usuario;

	//bi-directional many-to-one association to Movimiento
	@OneToMany(mappedBy="cuenta")
	private List<Movimiento> movimientos;

	//bi-directional many-to-one association to Transferencia
	@OneToMany(mappedBy="cuenta1")
	private List<Transferencia> transferencias1;

	//bi-directional many-to-one association to Transferencia
	@OneToMany(mappedBy="cuenta2")
	private List<Transferencia> transferencias2;

	public Cuenta() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getIban() {
		return this.iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public float getSaldo() {
		return this.saldo;
	}

	public void setSaldo(float saldo) {
		this.saldo = saldo;
	}

	public Divisa getDivisa() {
		return this.divisa;
	}

	public void setDivisa(Divisa divisa) {
		this.divisa = divisa;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Movimiento> getMovimientos() {
		return this.movimientos;
	}

	public void setMovimientos(List<Movimiento> movimientos) {
		this.movimientos = movimientos;
	}

	public Movimiento addMovimiento(Movimiento movimiento) {
		getMovimientos().add(movimiento);
		movimiento.setCuenta(this);

		return movimiento;
	}

	public Movimiento removeMovimiento(Movimiento movimiento) {
		getMovimientos().remove(movimiento);
		movimiento.setCuenta(null);

		return movimiento;
	}

	public List<Transferencia> getTransferencias1() {
		return this.transferencias1;
	}

	public void setTransferencias1(List<Transferencia> transferencias1) {
		this.transferencias1 = transferencias1;
	}

	public Transferencia addTransferencias1(Transferencia transferencias1) {
		getTransferencias1().add(transferencias1);
		transferencias1.setCuenta1(this);

		return transferencias1;
	}

	public Transferencia removeTransferencias1(Transferencia transferencias1) {
		getTransferencias1().remove(transferencias1);
		transferencias1.setCuenta1(null);

		return transferencias1;
	}

	public List<Transferencia> getTransferencias2() {
		return this.transferencias2;
	}

	public void setTransferencias2(List<Transferencia> transferencias2) {
		this.transferencias2 = transferencias2;
	}

	public Transferencia addTransferencias2(Transferencia transferencias2) {
		getTransferencias2().add(transferencias2);
		transferencias2.setCuenta2(this);

		return transferencias2;
	}

	public Transferencia removeTransferencias2(Transferencia transferencias2) {
		getTransferencias2().remove(transferencias2);
		transferencias2.setCuenta2(null);

		return transferencias2;
	}

}