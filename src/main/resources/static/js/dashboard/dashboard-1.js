(function($) {
    "use strict";
    function updateSalesOverviewChart(morrisChart) {
        $.ajax({
            url : "/api/today/income/overview",
            // url : "/api/today/test/trigger",
            // type: "POST",
            dataType : "json",
            success : function(datas, status) {
                morrisChart.setData(datas);
                morrisChart.redraw();
            }
        })
    }
    
    function updateGuestClassificationChart(pieChart) {
        $.ajax({
            url : "/api/today/guest/info",
            dataType : "json",
            success : function(datas, status) {
                pieChart.data.datasets[0]['data'] = datas.flatMap(element => element['value']);
                pieChart.data.labels = datas.flatMap(element => {
                    const date = new Date(Date.parse(element['label']));
                    return date.getFullYear()+"-"+date.getMonth()+"-"+date.getDate();
                });
                pieChart.update();
            }
        })
    }

    // Morris bar chart
    const salesOverviewChart = Morris.Bar({
        element: 'morris-bar-chart',
        data: [],
        xkey: 'hour',
        ykeys: ['income'],
        labels: ['Income'],
        barColors: ['#5873FE'],
        // barColors: ['#5873FE', '#343957', ],
        hideHover: 'auto',
        gridLineColor: '#eef0f2',
        resize: true
    });
    updateSalesOverviewChart(salesOverviewChart);
    setInterval(function() {
        updateSalesOverviewChart(salesOverviewChart);
    }, 60000);
    console.log("SalesOverview : ", salesOverviewChart);


    var guestClassificationCanvas = $("#guest-classification");
    // nk.height = 50
    var guestClassificationChart = new Chart(guestClassificationCanvas, {
        type: 'pie',
        data: {
            defaultFontFamily: 'Poppins',
            datasets: [{
                data: [],
                borderWidth: 0,
                backgroundColor: [
                    "rgba(89, 59, 219, .9)",
                    "rgba(89, 59, 219, .7)",
                    "rgba(89, 59, 219, .5)",
                    "rgba(89, 59, 219, .07)"
                ],
                hoverBackgroundColor: [
                    "rgba(89, 59, 219, .9)",
                    "rgba(89, 59, 219, .7)",
                    "rgba(89, 59, 219, .5)",
                    "rgba(89, 59, 219, .07)"
                ]

            }],
            labels: []
        },
        options: {
            responsive: true,
            legend: false,
            maintainAspectRatio: false
        }
    });
    updateGuestClassificationChart(guestClassificationChart);
    setInterval(function() {
        updateGuestClassificationChart(guestClassificationChart);
    }, 60000);
    console.log("Guest Classification : ", guestClassificationChart);

    var customerFeedbackProgress = $('#customer-feedback').circleProgress({
        value: 0.70,
        size: 100,
        fill: {
            gradient: ["#a389d5"]
        }
    });
    console.log("CustomerFeedback : ", customerFeedbackProgress);

    function updateTodaysIncome(progressBar, statDigit) {
        $.ajax({
            url : "/api/today/total/income",
            dataType : "json",
            success : function(data, status) {
                progressBar.style.setProperty("width", parseInt(data['progress']*100)+"%", "important");
                statDigit.getElementsByTagName("span")[0].innerText=data['value'];
                console.log(data);
                console.log(progressBar);
                console.log(statDigit);
            }
        })
    }

    var todaysIncomeBar = $(".progress-bar")[0];
    var todaysIncomeDigit = $(".stat-digit")[0];
    updateTodaysIncome(todaysIncomeBar, todaysIncomeDigit);
    setInterval(function() {
        updateTodaysIncome(todaysIncomeBar, todaysIncomeDigit);
    }, 60000);

})(jQuery);

// (function($) {
//     "use strict";

//     var data = [],
//         totalPoints = 300;

//     function getRandomData() {

//         if (data.length > 0)
//             data = data.slice(1);

//         // Do a random walk

//         while (data.length < totalPoints) {

//             var prev = data.length > 0 ? data[data.length - 1] : 50,
//                 y = prev + Math.random() * 10 - 5;

//             if (y < 0) {
//                 y = 0;
//             } else if (y > 100) {
//                 y = 100;
//             }

//             data.push(y);
//         }

//         // Zip the generated y values with the x values

//         var res = [];
//         for (var i = 0; i < data.length; ++i) {
//             res.push([i, data[i]])
//         }

//         return res;
//     }

//     // Set up the control widget

//     var updateInterval = 30;
//     $("#updateInterval").val(updateInterval).change(function() {
//         var v = $(this).val();
//         if (v && !isNaN(+v)) {
//             updateInterval = +v;
//             if (updateInterval < 1) {
//                 updateInterval = 1;
//             } else if (updateInterval > 3000) {
//                 updateInterval = 3000;
//             }
//             $(this).val("" + updateInterval);
//         }
//     });

//     var plot = $.plot("#cpu-load", [getRandomData()], {
//         series: {
//             shadowSize: 0 // Drawing is faster without shadows
//         },
//         yaxis: {
//             min: 0,
//             max: 100
//         },
//         xaxis: {
//             show: false
//         },
//         colors: ["#007BFF"],
//         grid: {
//             color: "transparent",
//             hoverable: true,
//             borderWidth: 0,
//             backgroundColor: 'transparent'
//         },
//         tooltip: true,
//         tooltipOpts: {
//             content: "Y: %y",
//             defaultTheme: false
//         }


//     });

//     function update() {

//         plot.setData([getRandomData()]);

//         // Since the axes don't change, we don't need to call plot.setupGrid()

//         plot.draw();
//         setTimeout(update, updateInterval);
//     }

//     update();


// })(jQuery);


// const wt = new PerfectScrollbar('.widget-todo');
// const wtl = new PerfectScrollbar('.widget-timeline');