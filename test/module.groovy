def read_properties(properties_file) {
    def props = readProperties interpolate: true, file: properties_file
    props.each {
        println ( it.key + " = " + it.value )
    }
}