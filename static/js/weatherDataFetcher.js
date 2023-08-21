function fetchWeatherData(latitude, longitude){
    var requestString = 'https://api.open-meteo.com/v1/forecast?latitude='+ latitude +"&longitude="+ longitude + "&current_weather=true";
    console.log(requestString);
    fetch(requestString, {method :'get'}).
        then(res => res.json()).
        then(data => {
            let current_weather = "Current weather: \nTemperature: "+data.current_weather.temperature + " Wind Speed: " + data.current_weather.windspeed + " Wind Direction: " +data.current_weather.winddirection;
            document.getElementById('currentWeather').innerHTML = current_weather;
        }).
        catch(err => {
              console.log(err);
        });
}