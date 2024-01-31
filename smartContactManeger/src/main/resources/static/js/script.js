console.log("This is script field")

const toggleSideBar = () => {
    if ($(".sidebar").is(":visible")) {
        //true
        // band krna hai 
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        // false
        // show krna hai
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};


const search = () => {
    let query = $("#search-input").val();
    // console.log(query)

    if (query == "") {

        $(".search-result").hide();

    } else {
        //search
        console.log(query);
        // sending req to server 

        let url = `http://localhost:8282/search/${query}`;

        fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                // data
                console.log(data);
                let text = `<div class='list-group'>`;

                // data.forEach((contact) => {
                //     text+=`<a href='/user/${contact.Cid}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`;   
                // });
                // text+=`</div>`;

                data.forEach((contact) => {
                    text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-action'> ${contact.name} </a>`;
                });
                text += `</div>`;

                $(".search-result").html(text);
                $(".search-result").show();
            });
    }
};


// first request to server to cerate order 



const paymentStart = () => {
    console.log("payment start...");
    var amount = $("#payment_field").val();
    console.log(amount);
    if (amount == '' || amount == null) {
        swal("Failed !!", "amount is required !!", "error");
        return;
    }

    // code 
    // we will use ajax to send request to server to create order - jQuery
    // project mai base.html mai head mai jquery ka version cdn link lgana hoga cdn

    $.ajax({
        url: '/user/create_order',  // User Controller mai bnanaya hai
        data: JSON.stringify({ amount: amount, info: 'order_request' }),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success: function (response) {
            // this function invoked where success
            console.log(response);

            // razor pay ki abt ki coding jab order ko to.string() se yahan aaya 
            if (response.status == 'created') {
                // open payment form
                let options = {
                    key: "Your ID razor pay mai generate hui thi", // Enter the Key ID generated from the Dashboard
                    amount: response.amount,
                    currency: "INR",
                    name: "Smart Contact Manager",
                    description: "Donation",
                    Image: "C:\Users\SU40091400\Desktop\SpringBootProject\IMG5.png",
                    order_id: response.id,
                    handler: function (response) {
                        console.log(response.razorpay.payment_id);
                        console.log(response.razorpay.razorpay_order_id);
                        console.log(response.razorpay_signature);
                        console.log("payment successful !!");
                        //alert("Congrates !! Payment Successful !!");

                        updatePaymentOnServer(response.razorpay.payment_id, response.razorpay.razorpay_order_id, "paid");
                      //  swal("Good job!", "Congrates !! Payment Successful !!", "success");

                    },
                    prefill: {
                        name: "",
                        email: "",
                        contact: ""
                    },
                    notes: {
                        address: "Nagpurian Suraj",
                    },
                    theme: {
                        color: "#3399cc",
                    },
                };

                let rzp = new Razorpay(options);
                rzp.on('payment.failed', function (response) {
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    //alert("Oops payment failed !!")
                   // swal("Failed !!", "Oops payment failed !!", "error");
                });
                rzp.open();
            }
        },
        error: function (error) {
            // invoked when error
            console.log(error);
            alert("Something went wrong !!");
        }

    })

};

//
function updatePaymentOnServer(payment_id, order_id, status){
    $.ajax({
        url: '/user/update_order',  // User Controller mai bnanaya hai
        data: JSON.stringify({ payment_id: payment_id, order_id: order_id, status: status }),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success:function(response){
            swal("Good job!", "Congrates !! Payment Successful !!", "success");
        },
        error:function(error){
            swal("Failed !!", "Your payment is successful, but we did not get on server, we will contact you ASAP", "error");
        }
    })
}

