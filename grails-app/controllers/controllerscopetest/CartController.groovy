package controllerscopetest

import grails.core.GrailsApplication

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CartController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    // static scope = "prototype"

    public CartController() {
        println 'new CartController'
        println grailsApplication.config.get( 'grails.controllers.defaultScope' )
    }

    GrailsApplication grailsApplication

    int count = 0

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        count++
        println count

        respond Cart.list(params), model:[cartCount: Cart.count()]
    }

    def show(Cart cart) {
        respond cart
    }

    def create() {
        respond new Cart(params)
    }

    @Transactional
    def save(Cart cart) {
        if (cart == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (cart.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond cart.errors, view:'create'
            return
        }

        cart.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'cart.label', default: 'Cart'), cart.id])
                redirect cart
            }
            '*' { respond cart, [status: CREATED] }
        }
    }

    def edit(Cart cart) {
        respond cart
    }

    @Transactional
    def update(Cart cart) {
        if (cart == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (cart.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond cart.errors, view:'edit'
            return
        }

        cart.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'cart.label', default: 'Cart'), cart.id])
                redirect cart
            }
            '*'{ respond cart, [status: OK] }
        }
    }

    @Transactional
    def delete(Cart cart) {

        if (cart == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        cart.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'cart.label', default: 'Cart'), cart.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'cart.label', default: 'Cart'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
