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

    function pieChartLabelFunction(data) {
        return data.flatMap(element => { return element['label']; });
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
            // method : "POST",
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

    const chartColors = {
        red: 'rgb(255, 99, 132)',
        orange: 'rgb(255, 159, 64)',
        yellow: 'rgb(255, 205, 86)',
        green: 'rgb(75, 192, 192)',
        blue: 'rgb(54, 162, 235)',
        purple: 'rgb(153, 102, 255)',
        grey: 'rgb(201, 203, 207)'
    };
    const DATE = {
        SUN : {  name : "Sun",  value : 0 },
        MON : {  name : "Mon",  value : 1 },
        TUE : {  name : "Tue",  value : 2 },
        WED : {  name : "Wed",  value : 3 },
        THU : {  name : "THU",  value : 4 },
        FRI : {  name : "FRI",  value : 5 },
        SAT : {  name : "SAT",  value : 6 },
    }
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

    function gradientBackgroundColor(context) {
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

    const guestOverviewCanvas = $("#guest-overview-chart");
    const incomeOverviewCanvas = $("#income-overview-chart");
    const top5SymptomsCanvas = $("#top-5-symptoms-chart");
    const prescriptionCanvas = $("#prescription-chart");

    var guestOverviewChart = new Chart( guestOverviewCanvas, {
        type : 'bar',
        data : {
            labels : [],
            datasets: [{
                label : 'The visit number of guest per day',
                data : [],
                backgroundColor: [
                    "rgba(52, 57, 87, 1.0)",
                    "rgba(52, 57, 87, 0.8",
                    "rgba(52, 57, 87, 0.6)",
                    "rgba(52, 57, 87, 0.4)",
                    "rgba(52, 57, 87, 0.2)",
                    "rgba(52, 57, 87, 0.08)",
                    "rgba(52, 57, 87, 0.06)"
                ]//'#5873FE' // gradientBackgroundColor
            }]
        }
    });

    var incomeOverviewChart = new Chart(incomeOverviewCanvas, {
        type : 'line',
        data: {
            labels : [],
            datasets: [
                {
                    label: 'My First Dataset',
                    data : [],
                    fill: {
                        target: true,
                        below : "rgba(89, 59, 219, .9)"
                    },
                    // borderColor: 'rgb(75, 192, 192)',
                    // borderColor: 
                    tension: 0.1
                }
            ]
        }
    });
    
    
    var top5SymptomsChart = new Chart(top5SymptomsCanvas, {
        type : 'pie',
        data: {
            labels : [],
            datasets: [
                {
                    label: 'My First Dataset',
                    data : [],
                    borderWidth: 0,
                    backgroundColor: [
                        "rgba(89, 59, 219, .9)",
                        "rgba(89, 59, 219, .7)",
                        "rgba(89, 59, 219, .5)",
                        "rgba(89, 59, 219, .2)",
                        "rgba(89, 59, 219, .07)"
                    ],
                    hoverBackgroundColor: [
                        "rgba(89, 59, 219, .9)",
                        "rgba(89, 59, 219, .7)",
                        "rgba(89, 59, 219, .5)",
                        "rgba(89, 59, 219, .2)",
                        "rgba(89, 59, 219, .07)"
                    ]
                    // backgroundColor : gradientBackgroundColor
                }
            ]
        },
        options: {
            responsive: true,
            legend: false,
            // maintainAspectRatio: false
        }
    });
    
    var prescriptionChart = new Chart(prescriptionCanvas, {
        type : 'pie',
        data: {
            labels : [],
            datasets: [
                {
                    label: 'My First Dataset',
                    data : [],
                    borderWidth: 0,
                    backgroundColor : [ 
                        "rgba(52, 57, 87, 1.0)",
                        "rgba(52, 57, 87, 0.85",
                        "rgba(52, 57, 87, 0.70)",
                        "rgba(52, 57, 87, 0.55)",
                        "rgba(52, 57, 87, 0.40)",
                        "rgba(52, 57, 87, 0.25)",
                        "rgba(52, 57, 87, 0.10)"
                    ],
                    hoverBackgroundColor: [
                        "rgba(52, 57, 87, 1.0)",
                        "rgba(52, 57, 87, 0.85",
                        "rgba(52, 57, 87, 0.70)",
                        "rgba(52, 57, 87, 0.55)",
                        "rgba(52, 57, 87, 0.40)",
                        "rgba(52, 57, 87, 0.25)",
                        "rgba(52, 57, 87, 0.10)"
                    ]
                }
            ]
        },
        options: {
            responsive: true,
            legend: false,
            // maintainAspectRatio: false
        }
    });

    // Initialize load data
    updateBarChart("/api/daily/guest/overview", guestOverviewChart, 
        convertGuestOverview, barChartLabelFunction, barChartDataFunction);
    updateBarChart("/api/daily/income/overview", incomeOverviewChart, 
        convertGuestOverview, barChartLabelFunction, barChartDataFunction);
    updateBarChart("/api/daily/top5/symptoms", top5SymptomsChart, 
        convertGuestOverview, pieChartLabelFunction, barChartDataFunction);
    updateBarChart("/api/daily/prescription", prescriptionChart, 
        convertGuestOverview, pieChartLabelFunction, barChartDataFunction);

    // Configure update interval 
    setInterval(function(){
            updateBarChart("/api/daily/guest/overview", guestOverviewChart, 
                convertGuestOverview, barChartLabelFunction, barChartDataFunction);        
    }, 60000);
    setInterval(function(){
        updateBarChart("/api/daily/income/overview", incomeOverviewChart, 
            convertGuestOverview, barChartLabelFunction, barChartDataFunction);        
    }, 60000);
    setInterval(function(){
        updateBarChart("/api/daily/top5/symptoms", top5SymptomsChart, 
            convertGuestOverview, pieChartLabelFunction, barChartDataFunction)
    }, 60000);
    setInterval(function(){
        updateBarChart("/api/daily/prescription", prescriptionChart, 
            convertGuestOverview, pieChartLabelFunction, barChartDataFunction);
    }, 60000);

})(jQuery);


// const wt2 = new PerfectScrollbar('.widget-todo2');