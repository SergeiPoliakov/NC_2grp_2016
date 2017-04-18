/**
 * Created by Костя on 26.03.2017.
 */

function markHashTag(text, container){

    var span = document.createElement('span');
    var linkText = document.createTextNode(text + " ");
    span.appendChild(linkText);
    span.className = "label label-default";
    var a = document.createElement('a');
    a.appendChild(span);
    a.href = "/search/" + text;

    container.appendChild(a);
    var space = document.createTextNode(" ");
    container.appendChild(space);
}

var tagContainers = document.getElementsByName("tags");

for (var i = 0; i < tagContainers.length; i++){
    var tags = tagContainers[i].innerHTML.split(" ");
    tagContainers[i].innerHTML = "";
    for (var j = 0; j < tags.length; j++) {
        if (j != 0)
            markHashTag(tags[j], tagContainers[i]);
    }
}


function linkClick(){
    alert(this.innerHTML);
}