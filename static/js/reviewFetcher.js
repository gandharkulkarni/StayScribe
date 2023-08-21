function fetchReviews(hotelId, username, isPageLoad, isNext){
    var requestString = 'fetchReviews?hotelId='+ hotelId;
    var pageNumber = 0;
    if(isPageLoad==true){
        requestString = requestString + '&offset=0&limit=10';
    }
    else{
        if(isNext==true){
            pageNumber = Number(document.getElementById('currentPage').textContent) + 1;
        }
        else{
            if(Number(document.getElementById('currentPage').textContent) - 1>=0){
                pageNumber = Number(document.getElementById('currentPage').textContent) - 1;
            }
        }
        var offset = Number(pageNumber)*10;
        requestString = requestString + '&offset='+offset+'&limit=10';
    }
    fetch(requestString, {method :'get'}).
    then(res => res.json()).
    then(data => {
        if(data.success==true){
            document.getElementById('currentPage').textContent  = pageNumber;
            var rows = "<thread><tr>"
                        +"<th>Title</th>"
                        +"<th>Review</th>"
                        +"<th>Rating</th>"
                        +"<th>User</th>"
                        +"<th>Date</th>"
                        +"<th>Action</th>"
                        +"</tr>";
            for (var i = 0; i < data.reviews.length; i++){
                rows = rows + "<tr>" +
                "<td>" + data.reviews[i].title + "</td>" +
                "<td>" + data.reviews[i].reviewText + "</td>" +
                "<td>" + data.reviews[i].overallRating + "</td>" +
                "<td>" + data.reviews[i].user + "</td>" +
                "<td>" + data.reviews[i].date + "</td>";

                if(username==data.reviews[i].user){
                rows = rows + "<td><a href=\"/editReview?hotelId="+hotelId+"&reviewId="+data.reviews[i].reviewId+"\"class=\"btn btn-outline-warning\"><span class=\"glyphicon glyphicon-pencil\"></span></a>"+
                "<a href=\"/deleteReview?hotelId="+hotelId+"&reviewId="+data.reviews[i].reviewId+"\"class=\"btn btn-outline-danger\"><span class=\"glyphicon glyphicon-trash\"></span></a></td>";
                }
                else{
                rows = rows + "<td></td>";
                }
                rows = rows +"</tr>";
            }
            rows = rows +"</thread>";
            document.getElementById('tablearea').innerHTML = rows;
        }
        else{
            if(isNext==true){
                document.getElementById('currentPage').textContent = pageNumber - 1;
            }
            if(isPageLoad==true){
                var message = "<h1>No reviews found</h1>";
                document.getElementById('currentPage').textContent = pageNumber;
                document.getElementById('reviewContent').innerHTML = message;
            }
        }
    }).
    catch(err => {
      console.log(err);
    });
};
