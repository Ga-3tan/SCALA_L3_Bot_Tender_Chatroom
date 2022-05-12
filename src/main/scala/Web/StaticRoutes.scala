package Web

/**
  * Assembles the routes dealing with static files.
  */
class StaticRoutes()(implicit val log: cask.Logger) extends cask.Routes:
    @cask.staticFiles("/static/resources")
    def staticRoutes() =  "src/main/resources"

    initialize()
end StaticRoutes
