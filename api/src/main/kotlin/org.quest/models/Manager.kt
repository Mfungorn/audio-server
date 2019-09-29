package org.quest.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

@Entity
@Table(name = "manager", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])]
)
class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null

    @Column(name = "name", nullable = false)
    lateinit var name: String

    @Email
    @Column(nullable = false)
    lateinit var email: String

    @Column(nullable = false)
    var emailVerified: Boolean = false

    @Column(name = "password")
    @JsonIgnore
    var password: String? = null

    @NotNull
    @Enumerated(EnumType.STRING)
    lateinit var provider: AuthProvider

    var providerId: String? = null
}