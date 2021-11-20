import {CategoryScale, Chart, registerables } from '../../vendor/chartjs/dist/chart.esm.js';
import * as helpers from '../../vendor/chartjs/dist/helpers.esm.js';
Chart.register(...registerables);

(function($) {
    "use strict"

    function barChartLabelFunction(data) {
        return data.flatMap(element => {
            const date = new Date(Date.parse(element['label']));
            return date.getFullYear()+"-"+date.getMonth()+"-"+date.getDate();
        })
    }

    function barChartDataFunction(data) {
        return data.flatMap(element => {
            return element['value'];
        });
    }

    function convertGuestOverview(data) {
        return data.slice(0, 7);
    }

    function updateBarChart(requestUrl, chart, convertDataCallback, labelCallback, dataCallback) {
        $.ajax({
            url : requestUrl,
            method : "POST",
            dataType : "json",
            success : function(data, status) {
                const convertedData = convertDataCallback(data);
                chart.data.labels = labelCallback(convertedData);
                chart.data.datasets[0]['data'] = dataCallback(convertedData);
                console.log(chart);
                chart.update();
            }
        });
    }

    // var data = {
    //     labels: ['facebook', 'twitter', 'youtube', 'google plus'],
    //     series: [{
    //                 value: 20,
    //                 className: "bg-facebook"
    //             },
    //             {
    //                 value: 10,
    //                 className: "bg-twitter"
    //             },
    //             {
    //                 value: 30,
    //                 className: "bg-youtube"
    //             },
    //             {
    //                 value: 40,
    //                 className: "bg-google-plus"
    //             }
    //         ]
    //         //        colors: ["#333", "#222", "#111"]
    // };

    // var options = {
    //     labelInterpolationFnc: function(value) {
    //         return value[0]
    //     }
    // };

    // var responsiveOptions = [
    //     ['screen and (min-width: 640px)', {
    //         chartPadding: 30,
    //         labelOffset: 100,
    //         labelDirection: 'explode',
    //         labelInterpolationFnc: function(value) {
    //             return value;
    //         }
    //     }],
    //     ['screen and (min-width: 1024px)', {
    //         labelOffset: 80,
    //         chartPadding: 20
    //     }]
    // ];

    // new Chartist.Pie('.ct-pie-chart', data, options, responsiveOptions);


    // /*----------------------------------*/

    // var data = {
    //     labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    //     series: [
    //         [5, 4, 3, 7, 5, 10, 3, 4, 8, 10, 6, 8],
    //         [3, 2, 9, 5, 4, 6, 4, 6, 7, 8, 7, 4],
    //         [4, 6, 3, 9, 6, 5, 2, 8, 3, , 5, 4],
    //     ]
    // };

    // var options = {
    //     seriesBarDistance: 10
    // };

    // var responsiveOptions = [
    //     ['screen and (max-width: 640px)', {
    //         seriesBarDistance: 5,
    //         axisX: {
    //             labelInterpolationFnc: function(value) {
    //                 return value[0];
    //             }
    //         }
    //     }]
    // ];

    // new Chartist.Bar('.ct-bar-chart', data, options, responsiveOptions);


    // $('.year-calendar').pignoseCalendar({
    //     theme: 'blue' // light, dark, blue
    // });

    const chartColors = {
        red: 'rgb(255, 99, 132)',
        orange: 'rgb(255, 159, 64)',
        yellow: 'rgb(255, 205, 86)',
        green: 'rgb(75, 192, 192)',
        blue: 'rgb(54, 162, 235)',
        purple: 'rgb(153, 102, 255)',
        grey: 'rgb(201, 203, 207)'
    };
    const colors = [chartColors.red, chartColors.orange, chartColors.yellow, chartColors.green, chartColors.blue, chartColors.purple, chartColors.grey];
    const cache = new Map();
    let width = null;
    let height = null;

    function createRadialGradient3(context, c1, c2, c3) {
        const chartArea = context.chart.chartArea;
        if (!chartArea) {
          // This case happens on initial chart load
          return;
        }
      
        const chartWidth = chartArea.right - chartArea.left;
        const chartHeight = chartArea.bottom - chartArea.top;
        if (width !== chartWidth || height !== chartHeight) {
          cache.clear();
        }
        let gradient = cache.get(c1 + c2 + c3);
        if (!gradient) {
          // Create the gradient because this is either the first render
          // or the size of the chart has changed
          width = chartWidth;
          height = chartHeight;
          const centerX = (chartArea.left + chartArea.right) / 2;
          const centerY = (chartArea.top + chartArea.bottom) / 2;
          const r = Math.min(
            (chartArea.right - chartArea.left) / 2,
            (chartArea.bottom - chartArea.top) / 2
          );
          const ctx = context.chart.ctx;
          gradient = ctx.createRadialGradient(centerX, centerY, 0, centerX, centerY, r);
          gradient.addColorStop(0, c1);
          gradient.addColorStop(0.5, c2);
          gradient.addColorStop(1, c3);
          cache.set(c1 + c2 + c3, gradient);
        }
      
        return gradient;
      }


    const guestOverviewCanvas = $("#guest-overview-chart");
    const incomeOverviewCanvas = $("#income-overview-chart");
    const top5SymptomsCanvas = $("#top-5-symptoms-chart");
    const prescriptionCanvas = $("#prescription-chart");

    var guestOverviewDatas = [];
    var guestOverviewLabels = [];
    var guestOverviewChart = new Chart( guestOverviewCanvas, {
        type : 'bar',
        data : {
            labels : guestOverviewLabels,
            datasets: [{
                label : 'The visit number of guest per day',
                data : guestOverviewDatas,
                backgroundColor: function(context) {
                    let c = colors[context.dataIndex];
                    if (!c) {
                        return;
                    }
                    if (context.active) {
                        c = helpers.getHoverColor(c);
                    }
                    const mid = helpers.color(c).desaturate(0.2).darken(0.2).rgbString();
                    const start = helpers.color(c).lighten(0.2).rotate(270).rgbString();
                    const end = helpers.color(c).lighten(0.1).rgbString();
                    return createRadialGradient3(context, start, mid, end);
                }
            }]
        }
    });

    updateBarChart("/api/daily/test/trigger", guestOverviewChart, 
        convertGuestOverview, barChartLabelFunction, barChartDataFunction);

    setInterval(function(){
            updateBarChart("/api/daily/test/trigger", guestOverviewChart, 
                convertGuestOverview, barChartLabelFunction, barChartDataFunction);        
    }, 60000);

    // $.ajax({
    //     url : "/api/daily/test/trigger",
    //     method : "POST",
    //     dataType : "json",
    //     success : function(data, status) {
    //         let slicedData = data.slice(0, 7);
    //         guestOverviewDatas = slicedData.flatMap(function(d) {
    //             return d['value'];
    //         });
    //         guestOverviewLabels = slicedData.flatMap(function(d){
    //             const date = new Date(Date.parse(Date(d['label'])));
    //             return date.getFullYear+"-"+date.getMonth()+"-"+date.getDate();
    //         });
    //         console.log(guestOverviewLabels, guestOverviewDatas, guestOverviewChart);
    //         guestOverviewChart.data.labels=guestOverviewLabels;
    //         guestOverviewChart.data.datasets[0]['data'] = guestOverviewDatas;
    //         guestOverviewChart.update();
    //     }
    // });


    var incomeOverviewChart = new Chart(incomeOverviewCanvas, {
        type : 'line',
        data: {
            labels : ['Sun','Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
            datasets: [
                {
                    label: 'My First Dataset',
                    data : [20,30,10,60,100,80,40],
                    fill: true,
                    borderColor: 'rgb(75, 192, 192)',
                    tension: 0.1
                }
            ]
        }
    });


    var top5SymptomsChart = new Chart(top5SymptomsCanvas, {
        type : 'pie',
        data: {
            labels : ['Sun','Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
            datasets: [
                {
                    label: 'My First Dataset',
                    data : [20,30,10,60,100,80,40],
                }
            ]
        }
    });

    var prescriptionChart = new Chart(prescriptionCanvas, {
        type : 'pie',
        data: {
            labels : ['Sun','Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
            datasets: [
                {
                    label: 'My First Dataset',
                    data : [20,30,10,60,100,80,40],
                }
            ]
        }
    });
})(jQuery);


// const wt2 = new PerfectScrollbar('.widget-todo2');