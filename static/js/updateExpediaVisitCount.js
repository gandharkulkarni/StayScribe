function updateExpediaVisitCount(hotelId, link){
    fetch('updateVisitCount?hotelId=' + hotelId+ '&link=' + link, {method :'get'}).
    then(res => res.text()).
    then(data => {
        console.log(data);
    }).
    catch(err => {
      console.log(err);
    });
};