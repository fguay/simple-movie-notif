$ ->
  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "movieupdate"
        updateMovie(message.message)
        console.log(message)
      else
        console.log(message)

  $("#addmovieform").submit (event) ->
    event.preventDefault()
    id = $("#addmovietext").val()
    # send the message to watch the movie
    ws.send(JSON.stringify({id: id}))
    updateMovie("Movie id " + id +  " added")
    # reset the form
    $("#addmovietext").val("")


updateMovie = (message) ->
  flipper = $("<div>").addClass("alert").addClass("alert-success").append("<strong>Success!</strong> " + message);
  $("#movies").prepend(flipper)
