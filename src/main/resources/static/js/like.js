/**
 * Created by wlassits on 2016. 12. 05..
 */
function toggle(el){
    if(el.className!="like")
    {
        el.src='images/like.png';
        el.className="like";
    }
    else if(el.className=="like")
    {
        el.src='images/notlike.png';
        el.className="notlike";
    }

    return false;
};