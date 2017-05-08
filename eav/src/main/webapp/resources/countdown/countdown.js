function get_timer_001(string_001) {
        var date_new_001 = string_001;
        var date_t_001 = new Date(date_new_001);
        var date_001 = new Date();
        var timer_001 = date_t_001 - date_001;
        if (date_t_001 > date_001) {
            var day_001 = parseInt(timer_001 / (60 * 60 * 1000 * 24));
            if (day_001 < 10) {
                day_001 = "0" + day_001;
            }
            day_001 = day_001.toString();
            var hour_001 = parseInt(timer_001 / (60 * 60 * 1000)) % 24;
            if (hour_001 < 10) {
                hour_001 = "0" + hour_001;
            }
            hour_001 = hour_001.toString();
            var min_001 = parseInt(timer_001 / (1000 * 60)) % 60;
            if (min_001 < 10) {
                min_001 = "0" + min_001;
            }
            min_001 = min_001.toString();
            var sec_001 = parseInt(timer_001 / 1000) % 60;
            if (sec_001 < 10) {
                sec_001 = "0" + sec_001;
            }
            sec_001 = sec_001.toString();
            timethis_001 = day_001 + " : " + hour_001 + " : " + min_001 + " : " + sec_001;
            $(".timerhello_001 p.result .result-day-001").text(day_001);
            $(".timerhello_001 p.result .result-hour-001").text(hour_001);
            $(".timerhello_001 p.result .result-minute-001").text(min_001);
            $(".timerhello_001 p.result .result-second-001").text(sec_001);
        } else {
            $(".timerhello_001 p.result .result-day-001").text("00");
            $(".timerhello_001 p.result .result-hour-001").text("00");
            $(".timerhello_001 p.result .result-minute-001").text("00");
            $(".timerhello_001 p.result .result-second-001").text("00");
        }
    }
	/*
    function getfrominputs_001() {
        string_001 = "05/11/2017 10:44"; // string_001 = "${timer_001}"; // 
        get_timer_001(string_001);
        setInterval(function () {
            get_timer_001(string_001);
        }, 1000);
    }
    $(document).ready(function () {
        getfrominputs_001();
    });
	*/



function get_timer_002(string_002) {
        var date_new_002 = string_002;
        var date_t_002 = new Date(date_new_002);
        var date_002 = new Date();
        var timer_002 = date_t_002 - date_002;
        if (date_t_002 > date_002) {
            var day_002 = parseInt(timer_002 / (60 * 60 * 1000 * 24));
            if (day_002 < 10) {
                day_002 = "0" + day_002;
            }
            day_002 = day_002.toString();
            var hour_002 = parseInt(timer_002 / (60 * 60 * 1000)) % 24;
            if (hour_002 < 10) {
                hour_002 = "0" + hour_002;
            }
            hour_002 = hour_002.toString();
            var min_002 = parseInt(timer_002 / (1000 * 60)) % 60;
            if (min_002 < 10) {
                min_002 = "0" + min_002;
            }
            min_002 = min_002.toString();
            var sec_002 = parseInt(timer_002 / 1000) % 60;
            if (sec_002 < 10) {
                sec_002 = "0" + sec_002;
            }
            sec_002 = sec_002.toString();
            timethis_002 = day_002 + " : " + hour_002 + " : " + min_002 + " : " + sec_002;
            $(".timerhello_002 p.result .result-day-002").text(day_002);
            $(".timerhello_002 p.result .result-hour-002").text(hour_002);
            $(".timerhello_002 p.result .result-minute-002").text(min_002);
            $(".timerhello_002 p.result .result-second-002").text(sec_002);
        } else {
            $(".timerhello_002 p.result .result-day-002").text("00");
            $(".timerhello_002 p.result .result-hour-002").text("00");
            $(".timerhello_002 p.result .result-minute-002").text("00");
            $(".timerhello_002 p.result .result-second-002").text("00");
        }
    }
    /*
	function getfrominputs_002() {
        string_002 = "05/12/2017 23:44"; // string_002 = "${timer_002}"; // 
        get_timer_002(string_002);
        setInterval(function () {
            get_timer_002(string_002);
        }, 1000);
    }
    $(document).ready(function () {
        getfrominputs_002();
    });
	*/
