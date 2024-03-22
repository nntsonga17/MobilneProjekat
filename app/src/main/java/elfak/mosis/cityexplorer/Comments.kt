package elfak.mosis.cityexplorer

data class Comments(var id:String="",
                    var author:String="",
                    var place:String="",
                    var grade:Int=0,
                    var comment:String="",
                    var date:String="",
                    var time:String="",
                    var positive:Int=0,
                    var negative:Int=0)
