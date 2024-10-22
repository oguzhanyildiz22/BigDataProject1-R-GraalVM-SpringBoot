library(lattice)
data <<- numeric(100)

function(dataHolder) {

svg()

    data <<- c(data[2:100],dataHolder$value)

    plot <- xyplot(randomData~time,
    data = data.frame(randomData = data, time = 0:99),
    main='Data Visualization',
    ylab = "Column12(x)", type = c('l','g'), col.line = 'sienna' )
    print(plot)

svg.off()
}