angular.module('app.services', []).factory('contasService', function($http) {

	var service = {};

	service.resumoContas = function(mes, ano, contas) {
		return $http({
			method : 'POST',
			url : 'rest/contas/resumoContas/' + mes + '/' + ano,
			data : angular.toJson(contas),
			headers : {
				'Content-Type' : 'application/json'
			}
		});
	}
	return service;

});