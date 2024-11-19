import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter

import javax.inject.Inject

class Filters @Inject() (corsFilter: CORSFilter)
  extends DefaultHttpFilters(corsFilter)