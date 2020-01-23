$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/create'
    get: '/get'

  vm = ko.mapping.fromJS
    name: ''
    getList: []

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.onSubmit = ->
    toastr.clear()
    if (!vm.name())
      toastr.error("Please enter a name")
      return no
    else
      data =
        name: vm.name()
      $.ajax
        url: apiUrl.send
        type: 'POST'
        data: JSON.stringify(data)
        dataType: 'json'
        contentType: 'application/json'
      .fail handleError
      .done (response) ->
        toastr.success(response)

  vm.getAllNames = ->
    $.ajax
      url: apiUrl.get
      type: 'GET'
    .fail handleError
    .done (response) ->
      console.log('1: ', vm.getList().length)
      vm.getList(response)
      console.log('2: ', vm.getList().length)


  ko.applyBindings {vm}