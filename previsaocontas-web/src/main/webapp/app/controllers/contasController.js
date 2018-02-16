angular
		.module('app.controllers', [])
		.controller(
				'contasController',
				function($scope, $http, $timeout, $q, $rootScope, contasService) {

					$scope.listaAnos = []
					$scope.indrUsuarioLogado = false
					$scope.conta = {
						id : null,
						data_registro : null,
						descricao : "",
						valor : 0,
						dia_vencimento : null,
						numr_agrupador : 0,
						qtde_parcelas : 1,
						numr_parcela : 0,
						usuario : null,
						contaPai : null,
						valorParciais : 0,
						tipo : "D",
						data_mes : new Date(),
						flag_comum : "N",
						tipoRepeticao : "ME"
					}
					$scope.usuarioLogado = {
						id : null,
						data_registro : null,
						login : "",
						senha : "",
						saldo : 0
					}
					$scope.contaFilha = JSON
							.parse(JSON.stringify($scope.conta))
					$scope.contaFilha.data_registro = new Date();

					$scope.contas = []
					$scope.contasFilhas = []
					$scope.userform = {
						usuario : "",
						senha : ""
					};

					$scope.msgs = {};

					$scope.msg = ""
					$scope.msgTitulo = "";
					$scope.msgClass = "";
					$scope.mes = 0;
					$scope.ano = 0;

					iniciarPrimeiraVez();

					function iniciarPrimeiraVez() {
						var promise = getUsuarioLogado();
						promise.then(function(r) {
							iniciar();
						}, function(r) {
							console.log(r);
						});
					}

					function iniciar() {

						var promiseListaAnos = listaAnos();

						promiseListaAnos.then(function(r) {

							$scope.mes = (new Date()).getMonth() + 1;
							$scope.ano = (new Date()).getFullYear();
							$scope.listarContas();
						}, function(r) {
							console.log(r);
						});

					}

					function msgErro(response) {
						console.log(response);
						if (response.config.url != null)
							delete $scope.msgs[response.config.url];

						if (response.data.mensagem != null) {
							$scope.msg = response.data.mensagem;
							if (response.status == 406) {
								$scope.msgTitulo = "Verifique!";
								$scope.msgClass = "alert alert-warning alert-dismissable fade in";
							} else if (response.status == 500) {
								$scope.msgTitulo = "Erro!";
								$scope.msgClass = "alert alert-danger alert-dismissable fade in";
							} else if (response.status == 409) {
								$scope.msgTitulo = "Info";
								$scope.msgClass = "alert alert-info alert-dismissable fade in";
							}
						} else {
							$scope.msg = "Erro desconhecido!"
							$scope.msgTitulo = "Erro!";
							$scope.msgClass = "alert alert-danger alert-dismissable fade in";
						}
						timeoutMsg(3000);
					}
					function msgSucesso(response) {
						if (response.config.url != null)
							delete $scope.msgs[response.config.url];

						if (response.data.mensagem != null) {
							if (response.status == 200) {
								$scope.msg = response.data.mensagem;
								$scope.msgTitulo = "Sucesso";
								$scope.msgClass = "alert alert-success alert-dismissable fade in";
							}
							timeoutMsg(3000);
						}
					}

					$scope.totalDespesaMes = 0;
					$scope.totalReceitaMes = 0;
					$scope.totalDespesaPrevistoMes = 0;
					$scope.totalReceitaPrevistoMes = 0;
					$scope.totalDespesaPrevistoAcumulado = 0;
					$scope.totalReceitaPrevistoAcumulado = 0;

					function resumoContas() {
						$scope.msgs['rest/contas/resumoContas/' + $scope.mes
								+ '/' + $scope.ano] = "Calculando previsão das contas...";
						contasService
								.resumoContas($scope.mes, $scope.ano,
										$scope.contas)
								.then(
										function successCallback(response) {
											msgSucesso(response);
											$scope.totalDespesaMes = response.data.totalDespesaMes;
											$scope.totalReceitaMes = response.data.totalReceitaMes;
											$scope.totalDespesaPrevistoMes = response.data.totalDespesaPrevistoMes;
											$scope.totalReceitaPrevistoMes = response.data.totalReceitaPrevistoMes;
											$scope.totalDespesaPrevistoAcumulado = response.data.totalDespesaPrevistoAcumulado;
											$scope.totalReceitaPrevistoAcumulado = response.data.totalReceitaPrevistoAcumulado;
										},
										function errorCallback(response) {
											msgErro(response);
											$scope.totalDespesaMes = 0;
											$scope.totalReceitaMes = 0;
											$scope.totalDespesaPrevistoMes = 0;
											$scope.totalReceitaPrevistoMes = 0;
											$scope.totalDespesaPrevistoAcumulado = 0;
											$scope.totalReceitaPrevistoAcumulado = 0;

										});

					}

					$scope.salvar = function() {

						$scope.msgs['rest/contas/salvar/'] = "Salvando conta...";
						contasService.salvar($scope.conta).then(
								function successCallback(response) {
									msgSucesso(response);
									$scope.listarContas();
									if ($scope.conta.id == null) {
										$scope.novaConta();
									} else {
										$scope.editarConta($scope.conta);
									}
								}, function errorCallback(response) {
									msgErro(response);
								});

					}

					$scope.alterarSaldo = function() {
						$scope.msgs['rest/usuario/alterarSaldo/'] = "Alterando saldo...";
						$http({
							method : 'POST',
							url : 'rest/usuario/alterarSaldo/',
							data : angular.toJson($scope.usuarioLogado),
							headers : {
								'Content-Type' : 'application/json'
							}
						}).then(function successCallback(response) {
							msgSucesso(response);
							$scope.listarContas();
						}, function errorCallback(response) {
							msgErro(response);
						});
					}

					$scope.salvarFilha = function() {

						$scope.msgs['rest/contas/salvar/'] = "Salvando conta filha...";

						$scope.contaFilha.data_registro = new Date();
						$scope.contaFilha.data_mes = $scope.conta.data_mes;
						$scope.contaFilha.flag_comum = $scope.conta.flag_comum;
						$scope.contaFilha.tipo = $scope.conta.tipo;
						$scope.contaFilha.status = "A";
						$scope.contaFilha.usuario = $scope.conta.usuario;
						$scope.contaFilha.contaPai = $scope.conta;

						contasService.salvar($scope.contaFilha).then(
								function successCallback(response) {
									msgSucesso(response);
									$scope.listarContasFilhas($scope.conta.id);
									$scope.novaContaFilha();
								}, function errorCallback(response) {
									msgErro(response)
								});
					}

					$scope.salvarTodos = function() {
						if (confirm('Salvar todas as parcelas relacionadas e não pagas iguais a essa?') == false) {
							return;
						}
						$scope.msgs['rest/contas/salvarTodos'] = "Salvando contas...";
						$http({
							method : 'POST',
							url : 'rest/contas/salvarTodos',
							data : angular.toJson($scope.conta),
							headers : {
								'Content-Type' : 'application/json'
							}
						})
								.then(
										function successCallback(response) {
											msgSucesso(response);
											$scope.listarContas();
											if (c.contaPai == null) {
												$scope
														.editarConta(response.data.objecto);
											} else {
												$scope
														.editarConta(response.data.objecto.contaPai);
											}
										}, function errorCallback(response) {
											msgErro(response)
										});
					}

					$scope.baixar = function(c) {
						$scope.msgs['rest/contas/baixar'] = "Baixando conta...";
						$http({
							method : 'POST',
							url : 'rest/contas/baixar',
							data : angular.toJson(c),
							headers : {
								'Content-Type' : 'application/json'
							}
						})
								.then(
										function successCallback(response) {
											msgSucesso(response);
											$scope.listarContas();
											if (c.contaPai == null) {
												$scope.cancelarEdicao();
											} else {
												$scope
														.editarConta(response.data.objecto.contaPai);
											}
										}, function errorCallback(response) {
											msgErro(response)
										});
					}

					$scope.cancelarBaixa = function(c) {
						$scope.msgs['rest/contas/cancelarBaixa'] = "Cancelando baixa de contas...";
						$http({
							method : 'POST',
							url : 'rest/contas/cancelarBaixa',
							data : angular.toJson(c),
							headers : {
								'Content-Type' : 'application/json'
							}
						})
								.then(
										function successCallback(response) {
											msgSucesso(response);
											$scope.listarContas();
											if (c.contaPai == null) {
												$scope.cancelarEdicao();
											} else {
												$scope
														.editarConta(response.data.objecto.contaPai);
											}
										}, function errorCallback(response) {
											msgErro(response)
										});
					}

					$scope.excluir = function(c) {
						if (confirm('Excluir conta (' + c.descricao + ')') == false) {
							return;
						}
						$scope.msgs['rest/contas/excluir'] = "Excluindo conta...";
						$http({
							method : 'DELETE',
							url : 'rest/contas/excluir',
							data : angular.toJson(c),
							headers : {
								'Content-Type' : 'application/json'
							}
						})
								.then(
										function successCallback(response) {
											msgSucesso(response);
											$scope.listarContas();
											if (c.contaPai == null) {
												$scope.cancelarEdicao();
											} else {
												$scope
														.editarConta(response.data.objecto.contaPai);
											}
										}, function errorCallback(response) {
											msgErro(response)
										});
					}

					$scope.excluirTodos = function() {
						if (confirm('Excluir todas as parcelas relacionadas e não pagas?') == false) {
							return;
						}
						$scope.msgs['rest/contas/excluirTodos'] = "Excluindo contas...";
						$http({
							method : 'DELETE',
							url : 'rest/contas/excluirTodos',
							data : angular.toJson($scope.conta),
							headers : {
								'Content-Type' : 'application/json'
							}
						}).then(function successCallback(response) {
							msgSucesso(response);
							$scope.listarContas();
							$scope.cancelarEdicao()
						}, function errorCallback(response) {
							msgErro(response)
						});
					}

					$scope.listarContas = function() {
						if ($scope.mes == null || $scope.ano == null) {
							return;
						}
						$scope.msgs['rest/contas/' + $scope.mes + '/'
								+ $scope.ano] = "Buscando contas...";

						$http.get(
								'rest/contas/' + $scope.mes + '/' + $scope.ano)
								.then(function successCallback(response) {
									msgSucesso(response);
									$scope.contas = response.data.objeto;
									resumoContas();
									
								}, function errorCallback(response) {
									msgErro(response);
									$scope.contas = [];

								});

					}

					$scope.listarContasFilhas = function(idPai) {
						$scope.msgs['rest/contas/contasFilhas/' + idPai] = "Buscando contas filhas...";
						$http({
							method : 'GET',
							url : 'rest/contas/contasFilhas/' + idPai
						}).then(function successCallback(response) {
							msgSucesso(response);
							$scope.contasFilhas = response.data;
						}, function errorCallback(response) {
							msgErro(response);
							$scope.contasFilhas = [];

						});
					}

					function listaAnos() {
						$scope.msgs['rest/anos'] = "Buscando anos...";
						var deferred = $q.defer();
						$http({
							method : 'GET',
							url : 'rest/anos'
						}).then(function successCallback(response) {
							deferred.resolve(response);
							msgSucesso(response);
							$scope.listaAnos = response.data.listaAnos;
						}, function errorCallback(response) {
							deferred.reject(response);
							msgErro(response);
							$scope.listaAnos = [];
						});

						return deferred.promise;
					}

					function getUsuarioLogado() {
						$scope.msgs['rest/login/usuarioLogado'] = "Buscando usuario logado...";
						var deferred = $q.defer();
						$http({
							method : 'GET',
							url : 'rest/login/usuarioLogado'
						})
								.then(
										function successCallback(response) {
											deferred.resolve(response);
											msgSucesso(response);
											$scope.usuarioLogado = response.data.objeto;
											$scope.userform.usuario = $scope.usuarioLogado.login;
											$scope.userform.senha = '';
											disableLogin();
											$scope.indrUsuarioLogado = true;
										}, function errorCallback(response) {
											deferred.reject(response);
											msgErro(response);
											$scope.usuarioLogado = null;
											$scope.indrUsuarioLogado = false;
										});

						return deferred.promise;
					}

					$scope.validaLogin = function() {
						$scope.msgs['rest/login/' + $scope.userform.usuario
								+ '/' + $scope.userform.senha] = "Validando login...";
						if ($scope.userform.usuario == ""
								|| $scope.userform.senha == "") {
							msgErro({
								config : {
									url : 'rest/login/'
											+ $scope.userform.usuario + '/'
											+ $scope.userform.senha
								},
								status : 406,
								data : {
									mensagem : 'Informar usuário e senha!'
								}
							});
							return;
						}
						$http(
								{
									method : 'GET',
									url : 'rest/login/'
											+ $scope.userform.usuario + '/'
											+ $scope.userform.senha
								}).then(function successCallback(response) {
							msgSucesso(response);
							iniciarPrimeiraVez();
						}, function errorCallback(response) {
							msgErro(response)
							$scope.contas = [];
							$scope.indrUsuarioLogado = false;
						});
					}

					$scope.logout = function() {
						$scope.msgs['rest/login/logout'] = "Saindo do sistema...";
						$http({
							method : 'GET',
							url : 'rest/login/logout'
						}).then(function successCallback(response) {
							msgSucesso(response);
							$scope.contas = [];
							enableLogin();
							$scope.userform.senha = '';
							$scope.indrUsuarioLogado = false;
						}, function errorCallback(response) {
							msgErro(response)
						});
					}

					function disableLogin() {
						document.getElementById("usuario").disabled = true;
						document.getElementById("senha").disabled = true;
						document.getElementById("submit").disabled = true;
					}

					function enableLogin() {
						document.getElementById("usuario").disabled = false;
						document.getElementById("senha").disabled = false;
						document.getElementById("submit").disabled = false;
					}

					$scope.editarConta = function(c) {
						$scope.conta = JSON.parse(JSON.stringify(c));
						$scope.conta.data_mes = new Date(c.data_mes);
						document.getElementById("despesa").disabled = true;
						document.getElementById("receita").disabled = true;
						document.getElementById("qtdeParcelas").disabled = true;
						document.getElementById("tipoRepeticao").disabled = true;
						$scope.listarContasFilhas(c.id);
						$('#myModal').modal('show');
					}

					$scope.cancelarEdicao = function() {
						limparEdicaoConta();
						$('#myModal').modal('hide');
					}

					function limparEdicaoConta() {
						$scope.conta = {
							id : null,
							data_registro : null,
							descricao : "",
							valor : 0,
							dia_vencimento : null,
							numr_agrupador : 0,
							qtde_parcelas : 1,
							numr_parcela : 1,
							usuario : null,
							contaPai : null,
							valorParciais : 0,
							tipo : "D",
							data_mes : new Date($scope.ano, $scope.mes - 1,
									new Date().getDate(),
									new Date().getHours(), new Date()
											.getMinutes(), new Date()
											.getSeconds()),
							flag_comum : "N",
							tipoRepeticao : "ME"
						}
						$scope.novaContaFilha();
						$scope.contasFilhas = [];
					}

					$scope.novaConta = function() {
						limparEdicaoConta();
						document.getElementById("despesa").disabled = false;
						document.getElementById("receita").disabled = false;
						document.getElementById("qtdeParcelas").disabled = false;
						document.getElementById("tipoRepeticao").disabled = false;
						$('#myModal').modal('show');
						document.getElementById('descricao').focus();
					}

					$scope.novaContaFilha = function() {
						$scope.contaFilha = {
							id : null,
							data_registro : new Date(),
							descricao : "",
							valor : 0,
							dia_vencimento : null,
							numr_agrupador : 0,
							qtde_parcelas : 1,
							numr_parcela : 1,
							usuario : null,
							contaPai : null,
							valorParciais : 0,
							tipo : "D",
							data_mes : new Date(),
							flag_comum : "N",
							tipoRepeticao : "ME"
						}
					}

					var promiseTimeoutMsg;

					function timeoutMsg(timeout) {

						$timeout.cancel(promiseTimeoutMsg);

						promiseTimeoutMsg = $timeout(function() {

							$scope.msg = "";

						}, timeout);

					}

					$scope.cssTitulo = function() {
						if ($scope.conta == null) {
							return;
						}
						if ($scope.conta.tipo == 'D') {
							return {
								// 'background-color' : '#f2dede'
								'color' : 'red',
								'font-weight' : 'bold',
								'font-size' : '18px'
							}
						} else {
							return {
								// 'background-color' : '#dff0d8'
								'color' : 'green',
								'font-weight' : 'bold',
								'font-size' : '18px'
							}
						}
					}

					$scope.cssLinhaTabela = function(c) {

						css = [];
						css.cursor = "pointer";

						if (c.tipo == 'D') {
							if (c.status == "B") {
								css.color = "#DCBCB9";
							} else {
								if (c.valorParciais > 0) {
									css.color = "#A94442";
								} else {
									css.color = 'red';
								}
							}
						} else {

							if (c.status == "B") {
								css.color = "#C2D8C2";
							} else {
								if (c.valorParciais > 0) {
									css.color = '#3D803E';
								} else {
									css.color = 'green';
								}
							}
						}

						return css;
					}

					$scope.isObjectEmpty = function(obj) {
						return Object.keys(obj).length === 0;
					}

					$scope.cssVencimento = function(c) {

						css = [];

						if (c.status != "B") {
							if (c.dia_vencimento > 0) {
								var data = new Date($scope.ano, $scope.mes - 1,
										c.dia_vencimento);
								if (new Date() >= data) {
									css.background = 'black';
								}
							}
						}

						return css;
					}

					$scope.mesSeguinte = function() {
						var data = new Date($scope.ano, $scope.mes, 1);
						$scope.mes = data.getMonth() + 1;
						$scope.ano = data.getFullYear();
						$scope.listarContas();
						$scope.cancelarEdicao();
					}
					$scope.mesAnterior = function() {
						var data = new Date($scope.ano, $scope.mes - 2, 1);
						$scope.mes = data.getMonth() + 1;
						$scope.ano = data.getFullYear();
						$scope.listarContas();
						$scope.cancelarEdicao();
					}

					$scope.tamanhoModal = function() {
						if (screen.width >= 768) {
							return {
								'width' : '35%'
							}
						} else {
							return {
								'width' : '80%'
							}
						}
					}

				});