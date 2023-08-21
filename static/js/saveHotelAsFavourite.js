function favouriteHotel(hotelId){
    fetch('favouriteHotel?hotelId=' + hotelId, {method :'get'}).
        then(res => res.text()).
        then(data => {
            if(data.trim() == "true"){
                document.getElementById('favouriteHotel').innerHTML = "<div class=\"alert alert-info\" role=\"alert\">Hotel added to favourites list</div>";
            }
            else{
                document.getElementById('favouriteHotel').innerHTML = "<div class=\"alert alert-warning\" role=\"alert\">Hotel already added to favourites list</div>";
            }
        }).
        catch(err => {
          console.log(err);
        });
}